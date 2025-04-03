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

}
