package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("WordControllerIntegrationTest.sql")
@Transactional
public class WordControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void test_showWordList() throws Exception {
		mockMvc.perform(get("/wordList"))
				.andExpect(content().string(containsString("テストカテゴリ")));
	}

	@Test
	//登録確認画面
	public void test_registConfirm_Success() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "newWordName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", ""))
				.andExpect(view().name("regist_confirm"));
	}

	@Test
	//登録実行 ( 成功 )
	public void test_regist_Success() throws Exception {
		mockMvc.perform(post("/regist")
				.param("wordName", "newWordName2")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", ""))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/wordList?categoryId=1&id=100"))//idの自動採番は100からに設定済み
				.andExpect(flash().attributeExists("regist_ok"));
	}
	@Test
	//登録実行 ( バリデーションエラー null )
	public void test_registConfirm_ValidationError() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "") 
				.param("content", "")
				.param("categoryId", "") 
				.param("categoryName", "")
				.param("relatedWordIds", ""))
			.andExpect(model().attributeHasFieldErrors("wordForm", "wordName", "content"))
			.andExpect(view().name("regist_form")); 
	}
	@Test
	//登録実行 ( バリデーションエラー 既存wordNameを入力 )
	public void test_registConfirm_DuplicateWordName() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "テストワード") // 既存wordName
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", ""))
			.andExpect(model().attributeHasFieldErrors("wordForm", "wordName"))
			.andExpect(view().name("regist_form")); 
	}
	@Test
	//登録実行 ( バリデーションエラー 自身を関連語に指定 )
	public void test_registConfirm_SameRelatedWord() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "テストワード")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "1")) // 自身のwordNameを選択
			.andExpect(model().attributeHasFieldErrors("wordForm", "relatedWordIds"))
			.andExpect(view().name("regist_form"));
	}
	
	@Test
	//登録実行 ( バリデーションエラー categoryIdとcategoryNameを同時入力 )
	public void test_registConfirm_categoryError() throws Exception {
		mockMvc.perform(post("/registConfirm")
				.param("wordName", "テストワード")
				.param("content", "content")
				.param("categoryId", "1") //categoryId入力
				.param("categoryName", "categoryName") //categoryName入力
				.param("relatedWordIds", "1")) 
		.andExpect(model().attributeHasFieldErrors("wordForm", "categoryId"))
		.andExpect(view().name("regist_form"));
	}




}
