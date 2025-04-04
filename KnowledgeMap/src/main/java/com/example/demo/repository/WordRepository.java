package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
    public List<Word> findByCategoryId(Integer categoryId);
//    public void deleteById(Integer id);
    
}

