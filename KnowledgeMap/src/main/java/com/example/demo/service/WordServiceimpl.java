package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class WordServiceimpl implements WordService{
	private final WordRepository wordRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public void addWord(WordForm wordForm) {
		Word word = new Word();	
		word.setName(wordForm.getWord());
		word.setContent(wordForm.getContent());
		// wordForm型の の categoryId から Category型に変換して Word型にセット
		Optional<Category> categoryOpt =  categoryRepository.findById(wordForm.getCategoryId()); 
		word.setCategory(categoryOpt.get());	
		wordRepository.save(word);	
	}

	@Override
	public List<Word> findAll() {
		List<Word> words = wordRepository.findAll();
		return words;
	}

	@Override
	public Optional<Word> findById(Integer id) {
		Optional<Word> wordOpt = wordRepository.findById(id);
		return wordOpt;
	}

	@Override
	public void delete(Word word) {
		wordRepository.delete(word);
	}

	@Override
	public void deleteById(Integer id) {
		wordRepository.deleteById(id);
	}

	@Override
	public void updateWord(Integer id, WordForm wordForm) {
		Optional<Word> wordOpt = wordRepository.findById(id);
		Word word = wordOpt.get();		
		word.setName(wordForm.getWord());
		word.setContent(wordForm.getContent());	
		// wordForm型の の categoryId から Category型に変換して Word型にセット
		Optional<Category> categoryOpt =  categoryRepository.findById(wordForm.getCategoryId()); 
		word.setCategory(categoryOpt.get());
		wordRepository.save(word);	
	}

}
