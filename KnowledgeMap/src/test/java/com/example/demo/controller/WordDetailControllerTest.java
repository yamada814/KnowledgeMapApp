package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.WebSecurityConfig;
import com.example.demo.advice.CommonExceptionHandler;
import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.exception.UnexpectedException;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;
import com.example.demo.validator.WordFormValidator;

@WebMvcTest(WordDetailController.class)
@Import({WebSecurityConfig.class,CommonExceptionHandler.class})
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
		@Bean
		WordFormValidator wordFormValidator() {
			return new WordFormValidator(wordService());
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
		word2.setRelatedWords(new ArrayList<>(List.of(word1)));
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
		// 編集したword
		Word updatedWord = new Word();
		updatedWord.setId(1);
		updatedWord.setWordName("updatedWordName");
		updatedWord.setContent("updatedcontent");
		updatedWord.setCategory(category1);
		updatedWord.setRelatedWords(list);
		doReturn(updatedWord).when(wordService).updateWord(eq(1), any());

		doReturn(list).when(wordService).findAll();
		doReturn(word1).when(wordService).findById(1);
		doReturn(word2).when(wordService).findById(2);
		doThrow(new UnexpectedException("指定された単語は存在しません")).when(wordService).findById(999);
		doReturn(Optional.empty()).when(wordService).findByWordName("newWordName");//word重複なし
		doReturn(Optional.of(newWord1)).when(wordService).findByWordNameAndWordbookId("ExistingWordName",1);//word重複あり

		doReturn(Optional.empty()).when(categoryService).findByNameAndWordbookId("newCategoryName",1);
		doReturn(Optional.of(category1)).when(categoryService).findByNameAndWordbookId("category1",1);
		doReturn(newCategory).when(categoryService).addCategory("newCategoryName", 1);
		doReturn(category1).when(categoryService).findByCategoryId(1);
		doReturn(newCategory).when(categoryService).findByCategoryId(2);
	}

	@Test
	//編集画面表示 成功
	void testShowEditForm() throws Exception {
		mockMvc.perform(get("/wordbooks/1/words/{id}/editForm", 1)
				.with(csrf())
				.with(user("testUser")))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeExists("word"));
	}

	@Test
	//編集画面表示 失敗
	void testShowEditForm_NotExistsWord() throws Exception {
		mockMvc.perform(get("/wordbooks/1/words/{id}/editForm", 999)
				.with(csrf())
				.with(user("testUser")))
				.andExpect(status().isOk())
				.andExpect(view().name("unexpected_error"));
	}

	@Test
	//編集画面表示 ( 遷移元がword_detail )
	void testShowEditForm_FromWordDetail() throws Exception {
		mockMvc.perform(get("/wordbooks/1/words/{id}/editForm", 1)
				.with(csrf())
				.with(user("testUser")))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeDoesNotExist("fromRegistConfirm"))
				.andExpect(content().string(containsString("word1")));
	}

	//編集画面表示 ( 遷移元がregist_confirm )
	void testShowEditForm_FromRegistConfirm() throws Exception {
		mockMvc.perform(get("/wordbooks/1/words/{id}/editForm", 1)
				.param("fromRegistConfirm", "true")
				.with(csrf())
				.with(user("testUser")))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_word"))
				.andExpect(model().attributeExists("fromRegistConfirm"))
				.andExpect(content().string(containsString("word1")));
	}

	@Test
	//編集確認 バリデーションエラー発生
	void testEditConfirm() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/{id}/editConfirm", 2)
				.with(csrf())
				.with(user("testUser"))
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
		mockMvc.perform(post("/wordbooks/1/words/{id}/editConfirm", 1)
				.with(csrf())
				.with(user("testUser"))
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "1")
				.param("categoryName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(1))));

		verify(categoryService, never()).addCategory("newCategoryName", 1);

	}

	@Test
	//編集実行 ( wordカブリなし,categoryName入力あり,categoryNameは未登録 -> categoryNameを新規追加 )
	void testEditConfirm_NotExistWord_InputCategoryName_NotExistsCategoryName() throws Exception {
		WordForm form = new WordForm();
		form.setWordName("newWordName");
		form.setContent("newContent");
		mockMvc.perform(post("/wordbooks/1/words/{id}/editConfirm", 1)
				.with(csrf())
				.with(user("testUser"))
				.param("wordName", "notExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryName", is("newCategoryName"))));
		verify(categoryService, atLeastOnce()).addCategory("newCategoryName", 1);

	}

	@Test
	//編集実行 ( wordカブリなし,categoryName入力あり,categoryNameは既存 -> 既存のcategoryIdで更新 )
	void testEditConfirm_NotExistWord_InputCategoryName_ExistingCategoryName() throws Exception {
		WordForm form = new WordForm();
		form.setWordName("newWordName");
		form.setContent("newContent");
		mockMvc.perform(post("/wordbooks/1/words/{id}/editConfirm", 1)
				.with(csrf())
				.with(user("testUser"))
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "category1"))//既存のcategoryNameを指定（category1のCategoryIdは 1 ）
				.andExpect(status().isOk())
				.andExpect(view().name("edit_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(1))));
		verify(categoryService, never()).addCategory("category1", 1);
	}

	@Test
	//編集確認 ( wordカブリあり -> 　入力フォームのビューを返し、エラーメッセージを表示 )
	void testEditConfirm_ExistingWord() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/{id}/editConfirm", 1)
				.with(csrf())
				.with(user("testUser"))
				.param("wordName", "ExistingWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName"))
				.andExpect(status().isOk())
				.andExpect(view().name("edit_form"))
				.andExpect(model().attributeHasFieldErrors("wordForm", "wordName"));
	}

	@Test
	//編集実行
	void testEdit() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/{id}/edit", 1)
				.with(csrf())
				.with(user("testUser"))
				.param("wordName", "updatedWordName")
				.param("content", "updatedContent")
				.param("categoryId", "1")
				.param("categoryName", ""))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=1"))
				.andExpect(flash().attributeExists("edit_ok"));
	}
}
