package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;

public interface WordService {
	public Optional<Word> findByWordName(String name);
	public void addWord(WordForm wordForm);
    public List<Word> findAll();
    public Optional<Word> findById(Integer id);
    public void delete(Word word);
    public boolean deleteById(Integer id);
    public void updateWord(Integer id, WordForm wordForm);
    public List<Word> findByCategoryId(Integer id);
    
}
