package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Category;

public interface CategoryService {
	public List<Category> findAll();
	public Optional<Category> findByCategoryId(Integer categoryId);
	public Integer addCategory(String categoryName);
	public Optional<Category> searchByName(String categoryName);
}
