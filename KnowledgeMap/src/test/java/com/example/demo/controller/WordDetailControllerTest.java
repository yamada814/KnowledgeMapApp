package com.example.demo.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

@WebMvcTest(WordDetailController.class)
public class WordDetailControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	WordService wordService;
	@Autowired
	CategoryService categoryService;

	@TestConfiguration
	static class testConfig {
		@Bean
		WordService wordService() {
			return org.mockito.Mockito.mock(WordService.class);
		}
		@Bean
		CategoryService categoryService() {
			return org.mockito.Mockito.mock(CategoryService.class);
		}
	}
	@BeforeEach
	void setupMockMvc() {
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		Category newCategory = new Category();
		newCategory.setId(2);
		newCategory.setName("newCategoryName");

		Word word1 = new Word();
		word1.setId(1);
		word1.setWordName("word1");
		word1.setCategory(category1);
		word1.setRelatedWords(List.of());
		Word word2 = new Word();
		word2.setId(2);
		word2.setWordName("word2");
		word2.setCategory(category1);
		word2.setRelatedWords(new ArrayList<Word>(List.of(word1)));
		List<Word> list = new ArrayList<>(List.of(word1, word2));

		Word newWord1 = new Word();
		newWord1.setId(3);
		newWord1.setWordName("newWordName1");
		newWord1.setCategory(category1);//既存カテゴリ -> addCategory()は呼ばれない
		newWord1.setRelatedWords(List.of());

		Word newWord2 = new Word();
		newWord2.setId(4);
		newWord2.setWordName("newWordName2");
		newWord2.setCategory(newCategory);//未登録カテゴリ -> addCategory()が呼ばれる
		newWord2.setRelatedWords(List.of());

		doReturn(list).when(wordService).findAll();
		doReturn(Optional.of(word1)).when(wordService).findById(1);
		doReturn(Optional.empty()).when(wordService).findById(999);
		doReturn(Optional.empty()).when(wordService).findByWordName("NotExistingWordName");//word重複なし
		doReturn(Optional.of(newWord1)).when(wordService).findByWordName("ExistingWordName");//word重複あり

		doReturn(Optional.of(category1)).when(categoryService).searchByName("category1");
		doReturn(Optional.empty()).when(categoryService).searchByName("newCategoryName");
		doReturn(newCategory).when(categoryService).addCategory("newCategoryName");
	}

	@Test
	void testShowDetail() throws Exception {
		doNothing().when(wordService).deleteById(1);
		mockMvc.perform(get("/wordDetail/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("word_detail"))
				.andExpect(model().attributeExists("word"))
				.andExpect(model().attribute("word", hasProperty("wordName", is("word1"))));
	}

	@Test
	//削除成功
	void testDeleteWord_ExistingWord() throws Exception {
		mockMvc.perform(post("/wordDetail/{id}/delete", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordList"))
				.andExpect(flash().attribute("delete_ok", "wordを削除しました"));
	}

	@Test
	//削除失敗(存在しないidを指定)
	void testDeleteWord_NotExistWord() throws Exception {
		mockMvc.perform(post("/wordDetail/{id}/delete", 999))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordList"))
				.andExpect(flash().attribute("delete_error", "指定されたwordは存在しません"));
		verify(wordService, never()).deleteById(999);
	}

	@Test
	//編集画面表示 成功
	void testShowEditForm() throws Exception {
		mockMvc.perform(get("/wordDetail/{id}/editForm", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeExists("word"));
	}

	@Test
	//編集画面表示 失敗
	void testShowEditForm_NotExistsWord() throws Exception {
		doReturn(Optional.empty()).when(wordService).findById(999);
		mockMvc.perform(get("/wordDetail/{id}/editForm", 999))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_error"));
	}
	@Test
	//編集画面表示 ( 遷移元がword_detail )
	void testShowEditForm_FromWordDetail() throws Exception {
		mockMvc.perform(get("/wordDetail/{id}/editForm", 1))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeDoesNotExist("fromRegistConfirm"))
				.andExpect(content().string(containsString("word1")));
	}
	//編集画面表示 ( 遷移元がregist_confirm )
	void testShowEditForm_FromRegistConfirm() throws Exception {
		mockMvc.perform(get("/wordDetail/{id}/editForm", 1)
				.param("fromRegistConfirm", "true"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_word"))
				.andExpect(model().attributeExists("fromRegistConfirm"))
				.andExpect(content().string(containsString("word1")));
	}
	@Test
	//編集確認 バリデーションエラー発生
	void testEditConfirm() throws Exception {
		mockMvc.perform(post("/wordDetail/{id}/editConfirm", 2)
				.param("id", "2")
				.param("wordName", "")
				.param("content", "")
				.param("categoryId", "")
				.param("categoryName", ""))
				.andExpect(model().attributeHasFieldErrors("wordForm", "wordName", "content", "categoryNotNull"));
	}
	@Test
	//編集実行 ( wordカブリなし,categoryName入力なし -> 入力されたwordFormの情報のまま更新 )
	void testEditConfirm_NotExistWord() throws Exception {
		WordForm form = new WordForm();
		form.setWordName("newWordName");
		form.setContent("newContent");
		form.setCategoryId(2);
		mockMvc.perform(post("/wordDetail/{id}/editConfirm", 1)
				.param("wordName", "notExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "2")
				.param("categoryName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(2))));;	
		verify(categoryService, never()).addCategory("newCategoryName");
//		ArgumentCaptor<WordForm> captor = ArgumentCaptor.forClass(WordForm.class);
//		verify(wordService).updateWord(eq(1), captor.capture());
//		WordForm argForm = captor.getValue();
//		assertThat(argForm.getWordName()).isEqualTo("newWordName");
	}
	@Test
	//編集実行 ( wordカブリなし,categoryName入力あり,categoryNameは未登録 -> categoryNameを新規追加 )
	void testEditConfirm_NotExistWord_InputCategoryName_NotExistsCategoryName() throws Exception {
		WordForm form = new WordForm();
		form.setWordName("newWordName");
		form.setContent("newContent");
		mockMvc.perform(post("/wordDetail/{id}/editConfirm", 1)
				.param("wordName", "notExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm",hasProperty("categoryName",is("newCategoryName"))));
		verify(categoryService, atLeastOnce()).addCategory("newCategoryName");
//		ArgumentCaptor<WordForm> captor = ArgumentCaptor.forClass(WordForm.class);
//		verify(wordService, atLeastOnce()).updateWord(eq(1), captor.capture());
//		WordForm argForm = captor.getValue();
//		assertThat(argForm.getCategoryId()).isEqualTo(2);
	}
	@Test
	//編集実行 ( wordカブリなし,categoryName入力あり,categoryNameは既存 -> 既存のcategoryIdで更新 )
	void testEditConfirm_NotExistWord_InputCategoryName_ExistingCategoryName() throws Exception {
		WordForm form = new WordForm();
		form.setWordName("newWordName");
		form.setContent("newContent");
		mockMvc.perform(post("/wordDetail/{id}/editConfirm", 1)
				.param("wordName", "notExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "category1"))//既存のcategoryNameを指定（category1のCategoryIdは 1 ）
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm",hasProperty("categoryId",is(1))));
		verify(categoryService, never()).addCategory("category1");
//		ArgumentCaptor<WordForm> captor = ArgumentCaptor.forClass(WordForm.class);
//		verify(wordService, atLeastOnce()).updateWord(eq(1), captor.capture());
//		WordForm argForm = captor.getValue();
//		assertThat(argForm.getCategoryId()).isEqualTo(1);//categoryIdは 1 で更新
	}
	@Test
	//編集確認 ( wordカブリあり -> 　入力フォームのビューを返し、かぶってるメッセージを表示 )
	void testEditConfirm_ExistingWord() throws Exception {
		mockMvc.perform(post("/wordDetail/{id}/editConfirm", 1)
				.param("wordName", "ExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeExists("word_duplicate"));
	}
	@Test
	//編集実行
	void testEdit() throws Exception{
		mockMvc.perform(post("/wordDetail/{id}/edit",1)
		.param("wordName", "NotExistingWordName")
		.param("content", "newContent")
		.param("categoryId", "2")
		.param("categoryName", ""))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/wordList"))
		.andExpect(flash().attributeExists("edit_ok"));
		ArgumentCaptor<WordForm> captor = ArgumentCaptor.forClass(WordForm.class);
		verify(wordService, atLeastOnce()).updateWord(eq(1), captor.capture());
		WordForm argForm = captor.getValue();
		assertThat(argForm.getCategoryId()).isEqualTo(2);
	}
}
