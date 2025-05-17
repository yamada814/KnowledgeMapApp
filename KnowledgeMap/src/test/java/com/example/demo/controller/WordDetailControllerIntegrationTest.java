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
	@Test
	public void test_edit() throws Exception{
		mockMvc.perform(post("/wordbooks/1/words/1/edit")
				.with(csrf())
				.param("id","1")
				.param("wordName", "updatedName")
				.param("content", "content")
				.param("categoryId", "1")
				.param("categoryName", "")
				.param("relatedWordIds", "2"))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/wordbooks/1/words?categoryId=1&id=1"))
		.andExpect(flash().attributeExists("edit_ok"));
	}

}
