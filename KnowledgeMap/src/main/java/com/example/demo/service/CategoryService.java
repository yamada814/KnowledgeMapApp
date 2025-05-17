package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Category;

public interface CategoryService {
	List<Category> findAll();
	List<Category> findByWordbookId(Integer wordbookId);
	Optional<Category> findByCategoryId(Integer categoryId);
	Category addCategory(String categoryName,Integer wordbookId);
	Optional<Category> findByName(String categoryName);
	Optional<Category> findByNameAndWordbookId(String categoryName,Integer wordbookId);
	boolean deleteByCategoryId(Integer categoryId);
}
