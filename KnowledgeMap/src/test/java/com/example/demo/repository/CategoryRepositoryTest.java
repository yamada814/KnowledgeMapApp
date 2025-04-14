package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entity.Category;

@DataJpaTest
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Test
	void testFindByNameTest() {
		Category category = new Category();
		category.setName("テストカテゴリー");
		categoryRepository.save(category);
		
		Optional<Category>  categoryOpt = categoryRepository.findByName(category.getName());
		assertThat(categoryOpt).isPresent();
		assertThat(categoryOpt.get().getName()).isEqualTo("テストカテゴリー");	
	}
}
