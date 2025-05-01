package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.WordDetailDto;
import com.example.demo.dto.WordDto;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;

public interface WordService {

	Optional<Word> findByWordName(String name);
	Word addWord(WordForm wordForm);
    List<Word> findAll();
    Optional<Word> findById(Integer id);
    boolean deleteById(Integer id);
    Word updateWord(Integer id, WordForm wordForm);
    List<WordDto> findWordsByCategoryId(Integer id);
    List<String> getRelatedWordNames(WordForm wordForm);
    WordDetailDto findWordDetailDtoById(Integer id);
    
}
