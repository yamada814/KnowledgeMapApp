package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
    public List<Word> findByCategoryId(Integer categoryId);
//    public void deleteById(Integer id);
    public Optional<Word> findByName(String name);
    
}

