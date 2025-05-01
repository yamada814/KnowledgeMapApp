package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
}
