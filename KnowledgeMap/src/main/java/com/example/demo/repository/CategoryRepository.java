package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Optional<Category> findByName(String name);
	Optional<Category> findByNameAndWordbookId(String name,Integer wordbookId);
	
	List<Category> findByWordbookId(Integer wordbookId);
	void deleteByWordbookId(Integer id);
}

