package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
    public Optional<Word> findByWordName(String name);
    public List<Word> findByCategoryId(Integer categoryId);
    
}

