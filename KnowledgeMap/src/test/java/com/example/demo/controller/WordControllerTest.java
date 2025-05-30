package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
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

@WebMvcTest(WordController.class)
@Import({WebSecurityConfig.class, CommonExceptionHandler.class})
public class WordControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	WordService wordService;
	@Autowired
	CategoryService categoryService;

	@TestConfiguration
	static class TestConfig {
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
		Category category2 = new Category();
		category2.setId(2);
		category2.setName("category2");
		//未登録カテゴリ
		Category newCategory = new Category();
		newCategory.setId(3);
		newCategory.setName("newCategoryName");

		Word word1 = new Word();
		word1.setId(1);
		word1.setWordName("word1");
		word1.setCategory(category1);
		Word word2 = new Word();
		word2.setId(2);
		word2.setWordName("word2");
		word2.setCategory(category1);

		List<Word> list = new ArrayList<>(List.of(word1, word2));
		//未登録word
		Word newWord = new Word();
		newWord.setId(3);
		newWord.setWordName("newWordName");
		word1.setCategory(newCategory);
		//登録済word
		Word existingWord = new Word();
		existingWord.setId(4);
		existingWord.setWordName("existingWordName");
		existingWord.setCategory(category1);
		//新規登録したword
		Word registedWord = new Word();
		registedWord.setId(5);
		registedWord.setWordName("newWordName");
		registedWord.setContent("content");
		registedWord.setCategory(category1);
		doReturn(registedWord).when(wordService).addWord(any());

		doReturn(list).when(wordService).findAll();
		doReturn(Optional.of(existingWord)).when(wordService).findByWordNameAndWordbookId("existingWordName", 1);
		doReturn(Optional.empty()).when(wordService).findByWordNameAndWordbookId("newWordName",1);//wordが未登録
		doReturn(word1).when(wordService).findById(1);
		doReturn(word2).when(wordService).findById(2);
		doReturn(newWord).when(wordService).findById(3);
		List<String> relatedWordNames = new ArrayList<>(List.of(word1.getWordName(), word2.getWordName()));
		doReturn(relatedWordNames).when(wordService).getRelatedWordNames(any());
		doThrow(new UnexpectedException("指定された単語が見つかりません")).when(wordService).findById(99);

		doReturn(Optional.of(category1)).when(categoryService).findByNameAndWordbookId("category1",1);//categoryNameが既存
		doReturn(Optional.empty()).when(categoryService).findByName("newCategoryName");//categoryNameが未登録
		doReturn(newCategory).when(categoryService).addCategory("newCategoryName", 1);//新規カテゴリを追加
		doReturn(category1).when(categoryService).findByCategoryId(1);//categoryIdによる検索
		doReturn(category2).when(categoryService).findByCategoryId(2);//categoryIdによる検索
		doReturn(newCategory).when(categoryService).findByCategoryId(3);//categoryIdによる検索
		doThrow(new UnexpectedException("指定されたカテゴリが見つかりません")).when(categoryService).findByCategoryId(99);

	}

	@Test
	//登録確認画面 バリデーションのチェック null
	void testRegistConfirm_Validation_Empty() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "")
				.param("content", "")
				.param("categoryId", "")
				.param("categoryName", "")
				.param("relatedWordIds", "2"))
				.andExpect(view().name("regist_form"))
				.andExpect(model().attributeHasFieldErrors(
						"wordForm", "wordName", "content", "categoryNotNull"));
	}
	@Test
	//登録確認画面 バリデーションのチェック時にResourceNotFoundException発生
	void testRegistConfirm_Validation_Exception() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "")
				.param("content", "")
				.param("categoryId", "")
				.param("categoryName", "")
				.param("relatedWordIds", "99")) //存在しないwordIdを関連語として指定された時
		.andExpect(view().name("unexpected_error"));
	}

	@Test
	//登録確認画面 バリデーションのチェック 既存word
	void testRegistConfirm_Validation_ExistingWord() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "existingWordName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", ""))
				.andExpect(view().name("regist_form"))
				.andExpect(model().attributeHasFieldErrors("wordForm", "wordName"))
				.andExpect(model().attribute("existingWord", hasProperty("wordName", is("existingWordName"))));
	}

	@Test
	//登録確認 ( categoryNameに入力あり ->  categoryNameで新規登録 -> そのcategoryIdをフォームにセット )
	void testRegistConfirm_InputCategoryName() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName")//未登録のcategoryNameを入力
				.param("relatedWordIds", "1", "2"))
				.andExpect(view().name("regist_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryName", is("newCategoryName"))))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(3))));
		verify(categoryService, atLeastOnce()).addCategory("newCategoryName", 1);
	}

	@Test
	//登録確認 (categoryNameに入力あり -> そのカテゴリーが既存   -> 既存のカテゴリーのcategoryIdをフォームにセット )
	void testRegistConfirm_InputCategoryName_ExistCategory() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "category1")//既存categoryNameを入力
				.param("relatedWordIds", ""))
				.andExpect(view().name("regist_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(1))))
				.andExpect(model().attribute("wordForm", hasProperty("categoryName", is("category1"))));
		verify(categoryService, never()).addCategory("category1", 1);
	}

	@Test
	//登録確認 (categoryName と categoryId の両方に入力あり -> バリデーションエラー)
	void testRegistConfirm_InputCategoryNameAndId() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "1")//catgoryNameに矛盾しないcategoryIdを入力
				.param("categoryName", "category1")//既存categoryNameを入力
				.param("relatedWordIds", ""))
				.andExpect(view().name("regist_form"))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(1))))
				.andExpect(model().attribute("wordForm", hasProperty("categoryName", is("category1"))));
		verify(categoryService, never()).addCategory("category1", 1);
	}

	@Test
	//登録確認 ( categoryIdに入力あり -> そのidで未登録 -> エラー画面 )
	void testRegistConfirm_InputCategoryId_NotExistCategory() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "99") //存在しないcategoryIdで登録
				.param("categoryName", "")
				.param("relatedWordIds", ""))
				.andExpect(view().name("unexpected_error"));
	}

	@Test
	//登録確認 ( wordNameによる検索 -> そのwordで未登録 -> 受け付けたwordFormの情報を表示 )
	void testRegistConfirm_NotExistword() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/registConfirm")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "1", "2"))
				.andExpect(view().name("regist_confirm"))
				.andExpect(model().attribute("wordForm", hasProperty("wordName", is("newWordName"))))
				.andExpect(model().attribute("wordForm", hasProperty("content", is("content"))))
				.andExpect(model().attribute("wordForm", hasProperty("categoryId", is(1))))
				.andExpect(model().attribute("relatedWordNames", containsInAnyOrder("word1", "word2")));
	}

	@Test
	// 登録 ( カテゴリ既存 -> word新規登録 -> wordListへリダイレクト )
	void testRegist_ExistCategory() throws Exception {
		WordForm wordform = new WordForm();
		wordform.setWordName("newWordName");
		wordform.setContent("content");
		wordform.setCategoryId(1);

		mockMvc.perform(post("/wordbooks/1/words/regist")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", wordform.getWordName())
				.param("content", wordform.getContent())
				.param("categoryId", String.valueOf(wordform.getCategoryId()))
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=5"))
				.andExpect(flash().attributeExists("regist_ok"));
	}

	@Test
	// 登録 ( 関連語入力 ->カテゴリ既存 -> word新規登録 -> wordListへリダイレクト )
	void testRegist_ExistCategory_relatedWords() throws Exception {
		mockMvc.perform(post("/wordbooks/1/words/regist")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("relatedWordIds", "1", "2"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=5"))
				.andExpect(flash().attributeExists("regist_ok"));
	}

	@Test
	// 登録 ( カテゴリ未登録 -> エラー )
	void testRegist_NotExistCategory() throws Exception {
		doThrow(new UnexpectedException("指定されたカテゴリが見つかりません")).when(wordService).addWord(any());
		mockMvc.perform(post("/wordbooks/1/words/regist")
				.with(csrf())
				.with(user("testUser")) 
				.param("wordName", "newWordName")
				.param("content", "content")
				.param("categoryId", "99"))
				.andExpect(view().name("unexpected_error"));
	}


}
