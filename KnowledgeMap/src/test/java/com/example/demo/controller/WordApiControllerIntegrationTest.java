package com.example.demo.controller;

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
@Sql("WordApiControllerIntegrationTest.sql")
@Transactional
public class WordApiControllerIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void test_apiShowWordDetail() throws Exception {
		mockMvc.perform(get("/api/words/1"))
				.andExpect(jsonPath("$.wordName").value("テストワード"))
				.andExpect(jsonPath("$.category.name").value("テストカテゴリ"));
	}
	
	@Test
	public void test_DeleteWord_Success() throws Exception {
		mockMvc.perform(delete("/api/words/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void test_DeleteWord_Fail() throws Exception {
		mockMvc.perform(delete("/api/words/2"))
				.andExpect(status().isNotFound());
	}
}
