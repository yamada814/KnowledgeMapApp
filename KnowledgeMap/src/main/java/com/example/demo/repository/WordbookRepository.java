package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Wordbook;

public interface WordbookRepository extends JpaRepository<Wordbook, Integer> {
	List<Wordbook> findByUserId(Integer userId);
	Wordbook save(Wordbook wordbook);
	
}
