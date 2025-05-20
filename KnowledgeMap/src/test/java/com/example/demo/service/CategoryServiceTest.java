package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;

import com.example.demo.entity.Category;

@SpringBootTest(webEnvironment=WebEnvironment.NONE)
@Sql("ServiceRepositoryIntegrationTest.sql")
@Transactional
public class CategoryServiceTest {
	@Autowired
	CategoryService categoryService;
	
	@Test
	void testAddCategory() {
		Category category = categoryService.addCategory("category2",1);
		assertThat(categoryService.findByCategoryId(category.getId()).getId()).isEqualTo(category.getId());
	}
}
