package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Word;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class WordApiController {
	private final WordService wordService;
	@GetMapping("/words")
	public List<Word> getWordsByCategoryId(@RequestParam("categoryId") Integer categoryId){
		return wordService.findByCategoryId(categoryId);
	}
}
