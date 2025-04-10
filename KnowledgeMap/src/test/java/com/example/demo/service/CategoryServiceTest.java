package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment=WebEnvironment.NONE)
@Sql("ServiceRepositoryIntegrationTest.sql")
@Transactional
public class CategoryServiceTest {
	@Autowired
	CategoryService categoryService;
	
	@Test
	void testAddCategory() {
		String categoryName = "category3";
		Integer id = categoryService.addCategory(categoryName);
		var found = categoryService.findByCategoryId(id);
		assertThat(found).isPresent();
		assertThat(found.get().getId()).isEqualTo(id);
	}
}
