package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;
	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}
	@Override
	public Optional<Category> findByCategoryId(Integer categoryId) {
		Optional<Category>  categoryOpt = categoryRepository.findById(categoryId);
		return categoryOpt;
	}
	//nameで検索
	@Override
	public Optional<Category> searchByName(String categoryName) {
		return categoryRepository.findByName(categoryName);
	}
	//nameで登録してそのidを返す
	@Override
	public Category addCategory(String categoryName) {
		Category category = new Category();
		category.setName(categoryName);
		Category savedCategory = categoryRepository.save(category);
		return savedCategory;
	}
	@Override
	public void deleteByCategoryId(Integer categoryId) {
		categoryRepository.deleteById(categoryId);
	}

}
