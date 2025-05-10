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
		String categoryName = "category3";
		Category category = categoryService.addCategory(categoryName,1);
		var found = categoryService.findByCategoryId(category.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getId()).isEqualTo(category.getId());
	}
}
