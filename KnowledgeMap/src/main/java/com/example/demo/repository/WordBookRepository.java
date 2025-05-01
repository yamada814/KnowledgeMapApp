package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WordBook;

public interface WordBookRepository extends JpaRepository<WordBook, Integer> {
	public List<WordBook> findByUserId(Integer userId);
}
