package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;
import com.example.demo.entity.WordBook;

public interface WordBookService {
	List<WordBook> findWordBook(User user);
}
