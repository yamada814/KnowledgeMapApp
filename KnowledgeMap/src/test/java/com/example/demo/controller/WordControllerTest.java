package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

@WebMvcTest(WordController.class)
public class WordControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	WordService wordService;
	@Autowired
	CategoryService categoryService;
	
	@TestConfiguration
	static class TestConfig{
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
		Word word1 = new Word();
		word1.setId(1);
		word1.setWordName("word1");
		word1.setCategory(category1);
		Word word2 = new Word();
		word2.setId(2);
		word2.setWordName("word2");
		word2.setCategory(category1);
		List<Word> list = new ArrayList<>(List.of(word1,word2));
		doReturn(list).when(wordService).findAll();	
		doReturn(Optional.of(category1)).when(categoryService).searchByName("category1");
 	}
	@Test
	void testShowWordList() throws Exception{
		mockMvc.perform(get("/wordList"))
				.andExpect(status().isOk())
				.andExpect(view().name("word_list"))
				.andExpect(content().string(containsString("word1")))
				.andExpect(content().string(containsString("word2")));
	}
	@Test
	//バリデーションのチェックが正しく行われるか
	void testRegistConfirm_validation() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "")
				.param("content", "")
				.param("categoryId", "")
				.param("categoryName", ""))
		.andExpect(view().name("regist_form"))
		.andExpect(model().attributeHasFieldErrors(
				"wordForm","wordName","content","categoryNotNull"));	
	}
	@Test
	//categoryNameに入力あり -> そのカテゴリーが未登録 -> categoryNameで新規登録 -> そのcategoryIdをフォームにセット
	void testRegistConfirm_InputCategoryName_NotExistsCategory() throws Exception {
		
		String newCategoryName = "newCategoryName";
		Category newCategory = new Category();
		newCategory.setId(2);
		newCategory.setName(newCategoryName);
		doReturn(Optional.empty()).when(categoryService).searchByName(newCategoryName);
		doReturn(2).when(categoryService).addCategory(newCategoryName);
		doReturn(Optional.of(newCategory)).when(categoryService).findByCategoryId(2);
		doReturn(Optional.empty()).when(wordService).findByWordName(any());
		
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "newCategoryName"))
		.andExpect(view().name("regist_confirm"))
		.andExpect(model().attribute("wordForm",hasProperty("categoryName",is("newCategoryName"))));
	}
	@Test
	//categoryNameに入力あり -> そのカテゴリーが既存   -> 既存のカテゴリーのcategoryIdをフォームにセット
	void testRegistConfirm_InputCategoryName_ExistsCategory() throws Exception {		
		Category exitingCategory = new Category();
		exitingCategory.setId(10);
		exitingCategory.setName("existingCategoryName");
		doReturn(Optional.of(exitingCategory)).when(categoryService).searchByName(exitingCategory.getName());
		doReturn(Optional.of(exitingCategory)).when(categoryService).findByCategoryId(10);
		doReturn(Optional.empty()).when(wordService).findByWordName(any());
		
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", "")
				.param("categoryName", "existingCategoryName"))
		.andExpect(view().name("regist_confirm"))
		.andExpect(model().attribute("wordForm",hasProperty("categoryId",is(10))))
		.andExpect(model().attribute("wordForm",hasProperty("categoryName",is("existingCategoryName"))));
	}
	@Test
	//categoryIdに入力あり -> そのidで未登録 -> エラー画面
	void testRegistConfirm_OptionCategoryId_NotExistsCategory() throws Exception {		
		int categoryId = 10;
		doReturn(Optional.empty()).when(categoryService).findByCategoryId(categoryId);
		doReturn(Optional.empty()).when(wordService).findByWordName(any());

		mockMvc.perform(post("/registConfirm")
				.param("wordName", "newWordName")
				.param("content", "newContent")
				.param("categoryId", Integer.toString(categoryId))
				.param("categoryName", "existingCategoryName"))
		.andExpect(view().name("regist_error"));
	}
	@Test
	//wordNameによる検索 -> そのwordが既存 -> そのwordの情報を表示
	void testRegistConfirm_Exitstingword() throws Exception {		
		String existingWordName = "existingWordName";
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		Word word1 = new Word();
		word1.setId(1);
		word1.setWordName(existingWordName);
		word1.setCategory(category1);
		doReturn(Optional.of(word1)).when(wordService).findByWordName(existingWordName);
		doReturn(Optional.of(category1)).when(categoryService).findByCategoryId(1);

		mockMvc.perform(post("/registConfirm")
				.param("wordName", existingWordName)
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", ""))
		.andExpect(view().name("regist_confirm"))
		.andExpect(model().attribute("word",hasProperty("wordName",is(existingWordName))));
	}
	@Test
	//wordNameによる検索 -> そのwordが未登録 -> 受け付けたwordFormの情報を表示
	void testRegistConfirm_NotExitsword() throws Exception {		
		String newWordName = "newWordName";
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		doReturn(Optional.empty()).when(wordService).findByWordName(newWordName);
		doReturn(Optional.of(category1)).when(categoryService).findByCategoryId(1);

		mockMvc.perform(post("/registConfirm")
				.param("wordName", newWordName)
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", ""))
		.andExpect(view().name("regist_confirm"))
		.andExpect(model().attribute("wordForm",hasProperty("wordName",is(newWordName))))
		.andExpect(model().attribute("wordForm",hasProperty("content",is("content"))))
		.andExpect(model().attribute("wordForm",hasProperty("categoryId",is(1))));
	}	
	@Test
	//word未登録 -> カテゴリ既存 -> word新規登録 -> wordListへリダイレクト
	void testRegist_NotExistsWord_ExitsCategory() throws Exception {
	    WordForm wordform = new WordForm();
	    wordform.setWordName("newWordName");
	    wordform.setContent("content");
	    wordform.setCategoryId(1);

	    Category category = new Category();
	    category.setId(1);
	    category.setName("category1");

	    doReturn(Optional.of(category)).when(categoryService).findByCategoryId(1);
	    doReturn(Optional.empty()).when(wordService).findByWordName("newWord");

	    mockMvc.perform(post("/regist")
	            .param("wordName", wordform.getWordName())
	            .param("content", wordform.getContent())
	            .param("categoryId", String.valueOf(wordform.getCategoryId()))
	    )
	    .andExpect(status().is3xxRedirection())
	    .andExpect(redirectedUrl("/wordList"))
	    .andExpect(flash().attributeExists("regist_ok"));
	}
	@Test
	//word既存 -> 既存のword情報をmodelに追加 -> regist_confirmへ
	void testRegist_ExistsWord() throws Exception {
	    String existingWordName = "existingWordName";
	    int categoryId = 1;

	    Category category = new Category();
	    category.setId(categoryId);
	    category.setName("category1");

	    Word existingWord = new Word();
	    existingWord.setId(1);
	    existingWord.setWordName(existingWordName);
	    existingWord.setContent("existingContent");
	    existingWord.setCategory(category);

	    List<Category> categoryList = List.of(category);

	    doReturn(Optional.of(category)).when(categoryService).findByCategoryId(categoryId);
	    doReturn(Optional.of(existingWord)).when(wordService).findByWordName(existingWordName);
	    doReturn(categoryList).when(categoryService).findAll();

	    mockMvc.perform(post("/regist")
	            .param("wordName", existingWordName)
	            .param("content", "content") 
	            .param("categoryId", String.valueOf(categoryId))
	    )
	    .andExpect(status().isOk())
	    .andExpect(view().name("regist_confirm"))
	    .andExpect(model().attribute("word", hasProperty("wordName", is(existingWordName))))
	    .andExpect(model().attribute("categories", categoryList));
	}
}

