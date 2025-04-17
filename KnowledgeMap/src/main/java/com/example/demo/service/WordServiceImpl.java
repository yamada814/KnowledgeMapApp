package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRelationRepository;
import com.example.demo.repository.WordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
	private final WordRepository wordRepository;
	private final CategoryRepository categoryRepository;
	private final WordRelationRepository wordRelationRepository;
	
	//WordForm型からWord型への変換を行うユーティリティメソッド
	public Word transferWordFormToWord(Word word,WordForm wordForm) {
		word.setWordName(wordForm.getWordName());
		word.setContent(wordForm.getContent());
		// wordForm型の categoryId から Category型に変換して Word型にセット
		Optional<Category> categoryOpt = categoryRepository.findById(wordForm.getCategoryId());
		word.setCategory(categoryOpt.get());
		//wordFormのidのリストから Wordのリスト に変換して wordにセット
		//@ManyToManyのついてるフィールドは、Hibernateが内部で clear()やadd()を行う可能性があるので mutable である必要がある
		List<Word> relatedWords = wordForm.getRelatedWordIds().stream()
				.map(wordId -> wordRepository.findById(wordId))
				.filter(opt -> opt.isPresent())
				.map(opt -> opt.get())
				.collect(Collectors.toCollection(ArrayList::new));
				// .toList()はimmutableとなり、clear()が呼ばれたときにjava.lang.UnsupportedOperationExceptionが発生する
		word.setRelatedWords(relatedWords);
		return word;	
	}
	
	// nameで検索
	@Override
	public Optional<Word> findByWordName(String name) {
		return wordRepository.findByWordName(name);
	}

	@Override
	public void addWord(WordForm wordForm) {
		Word word = new Word();
		transferWordFormToWord(word,wordForm);//WordForm型 -> Word型　の変換
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
	}

	@Override
	@Transactional
	public boolean deleteById(Integer id) {
		if(wordRepository.findById(id).isEmpty()) {
			return false;
		}
		wordRelationRepository.deleteByWordId(id);
		wordRelationRepository.deleteByRelatedWordId(id);
		wordRepository.deleteById(id);
		return true;
	}

	@Override
	public void updateWord(Integer id, WordForm wordForm) {
		Optional<Word> wordOpt = wordRepository.findById(id);
		Word word = wordOpt.get();
		transferWordFormToWord(word,wordForm);//WordForm型 -> Word型　の変換
		wordRepository.save(word);
	}

	@Override
	public List<Word> findByCategoryId(Integer id) {
		return wordRepository.findByCategoryId(id);
	}
}
