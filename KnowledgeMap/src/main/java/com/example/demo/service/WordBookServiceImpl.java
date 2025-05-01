package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.entity.WordBook;
import com.example.demo.repository.WordBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordBookServiceImpl implements WordBookService{
	
	private final WordBookRepository wordBookRepository;
	
	@Override
	public List<WordBook> findWordBook(User user) {
		return wordBookRepository.findByUserId(user.getId());
	}
}
