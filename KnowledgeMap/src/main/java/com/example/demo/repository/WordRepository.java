package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
    Optional<Word> findByWordName(String name);
    List<Word> findByCategoryId(Integer categoryId);
    List<Word> findByWordbookId(Integer wordbookId);
    Optional<Word> findByWordNameAndWordbookId(String name,Integer wordbookId);
    void deleteByWordbookId(Integer id);
    
}

