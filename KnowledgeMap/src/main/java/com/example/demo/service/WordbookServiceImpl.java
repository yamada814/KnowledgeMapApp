package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.User;
import com.example.demo.entity.Wordbook;
import com.example.demo.repository.WordbookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordbookServiceImpl implements WordbookService{
	
	private final WordbookRepository wordbookRepository;
	
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
	public Optional<Wordbook> findByWordbookNameAndUserId(String name,Integer userId) {
		return wordbookRepository.findByNameAndUserId(name,userId);
	}
}
