package com.example.demo.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;
import com.example.demo.repository.WordbookRepository;
import com.example.demo.service.LoginUserDetails;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("WordbookApiControllerIntegrationTest.sql")
@Transactional
public class WordbookApiControllerIntegrationTest {
	
	User user;
	LoginUserDetails userDetails;
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	WordbookRepository wordbookRepository;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1);
		user.setUsername("testUser");
		
		userDetails = new LoginUserDetails(user);
	}

	@Test
	// 新規追加 (正常)
	void testResgistWordbook_success() throws Exception{
		mockMvc.perform(post("/wordbooks/api/regist")
			.param("wordbookName","newWordbookName")
			.param("userId","1")
			.with(csrf())
			.with(user(userDetails)))	
			.andExpect(jsonPath("$.wordbookName").value("newWordbookName"));		
	}
	@Test
	// 新規追加 (バリデーションエラー null)
	void testResgistWordbook_validationError_null() throws Exception{
		mockMvc.perform(post("/wordbooks/api/regist")
				.param("wordbookName","")//wordbookNameがnull
				.param("userId","1")
				.with(csrf())
				.with(user(userDetails)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").value("単語帳名が空です"));	
	}
	@Test
	// 新規追加 (バリデーションエラー 既存wordbookName)
	void testResgistWordbook_validationError_exits() throws Exception{
		mockMvc.perform(post("/wordbooks/api/regist")
				.param("wordbookName","wordbook1")//登録済みのwordbbokNameを入力
				.param("userId","1")
				.with(csrf())
				.with(user(userDetails)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$").value("この単語帳名は既に登録されています"));	
	}
	
	@Test
	// 削除
	void testDelete() throws Exception {
		mockMvc.perform(delete("/wordbooks/api/delete/{id}",1)
		.with(csrf())
		.with(user(userDetails)))
		.andExpect(status().isOk());
		
		assertThat(wordbookRepository.findById(1)).isEmpty();	
	}
	@Test
	// 削除
	void testDelete_NotFound() throws Exception {
		mockMvc.perform(delete("/wordbooks/api/delete/{id}",2)
				.with(csrf())
				.with(user(userDetails)))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.error").value("指定された単語帳は見つかりません"));			
	}
	

}
