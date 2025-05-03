package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;
import com.example.demo.entity.Wordbook;

public interface WordbookService {
	List<Wordbook> findWordBook(User user);
	Wordbook save(Wordbook wordbook);
}
