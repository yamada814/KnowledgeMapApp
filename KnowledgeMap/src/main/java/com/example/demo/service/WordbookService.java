package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.User;
import com.example.demo.entity.Wordbook;

public interface WordbookService {
	List<Wordbook> findWordBook(User user);
	WordbookDto save(Wordbook wordbook);
	Optional<Wordbook> findByWordbookNameAndUserId(String name,Integer userId);
}
