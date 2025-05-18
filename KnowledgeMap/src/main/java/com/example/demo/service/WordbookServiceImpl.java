package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.User;
import com.example.demo.entity.Wordbook;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRelationRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.repository.WordbookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordbookServiceImpl implements WordbookService{
	
	private final WordbookRepository wordbookRepository;
	private final CategoryRepository categoryRepository;
	private final WordRepository wordRepository;
	private final WordRelationRepository wordRelationRepository;
	
	
	@Override
	public List<Wordbook> findWordBook(User user) {
		return wordbookRepository.findByUserId(user.getId());
	}

	@Override
	public WordbookDto save(Wordbook wordbook) {
		Wordbook savedWordbook =  wordbookRepository.save(wordbook);
		WordbookDto dto = new WordbookDto(savedWordbook.getId(),savedWordbook.getName());
		return dto;
	}

	@Override
	//既存wordかの確認用
	public Optional<Wordbook> findByWordbookNameAndUserId(String name,Integer userId) {
		return wordbookRepository.findByNameAndUserId(name,userId);
	}

	@Override
	@Transactional
	public boolean deleteById(Integer id) {
		Optional<Wordbook> wordbookOpt = wordbookRepository.findById(id);
		if(wordbookOpt.isPresent()) {
			wordRepository.deleteByWordbookId(id);
			categoryRepository.deleteByWordbookId(id);
			wordbookRepository.deleteById(id);
			return true;
		}
		return false;
	}	
}
