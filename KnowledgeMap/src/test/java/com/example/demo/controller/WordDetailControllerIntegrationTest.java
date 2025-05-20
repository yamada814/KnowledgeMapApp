package com.example.demo.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("WordDetailControllerIntegrationTest.sql")
@Transactional
@WithMockUser(username="testUser")
public class WordDetailControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	public void test_showEditForm() throws Exception{
		mockMvc.perform(get("/wordbooks/1/words/1/editForm")
				.with(csrf()))
		.andExpect(view().name("edit_form"))
		.andExpect(model().attribute("word", hasProperty("wordName",is("テストワード1"))));
	}
	//編集確認 (成功)
	@Test
	public void test_editConfirm_Success() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/editConfirm")
				.with(csrf())
				.param("wordName", "newName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "2"))
		.andExpect(view().name("edit_confirm"));
	}
	//編集確認 (存在しない単語idを編集)
	@Test
	public void test_editConfirm_Exception() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/99/editConfirm")
				.with(csrf())
				.param("wordName", "newName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "2"))
		.andExpect(view().name("unexpected_error"));
	}
	//編集確認 (新しいカテゴリを登録)
	@Test
	public void test_editConfirm_RegistCategory() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/editConfirm")
				.with(csrf())
				.param("wordName", "newName")
				.param("content", "content")
				.param("categoryId", "")
				.param("categoryName", "newCategory")
				.param("relatedWordIds", "2"))
		.andExpect(view().name("edit_confirm"));
	}
	//編集確認 (重複カテゴリ名)
	@Test
	public void test_editConfirm_RegistCategory_ExistingCategoryName() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/editConfirm")
				.with(csrf())
				.param("wordName", "newName")
				.param("content", "content")
				.param("categoryId", "")
				.param("categoryName", "テストカテゴリ")
				.param("relatedWordIds", "2"))
		.andExpect(view().name("edit_confirm"));
	}
	// 編集実行
	@Test
	public void test_edit() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/edit")
				.with(csrf())
				.param("wordName", "updatedName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "2"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=1"))
		.andExpect(flash().attributeExists("edit_ok"));
	}
	// 編集実行 ( 関連語なし )
	@Test
	public void test_edit_NotExistsRelatedWords() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/edit")
				.with(csrf())
				.param("wordName", "updatedName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", ""))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=1"))
		.andExpect(flash().attributeExists("edit_ok"));
	}

}
