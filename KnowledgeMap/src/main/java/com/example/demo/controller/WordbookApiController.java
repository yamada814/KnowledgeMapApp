package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.WordbookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WordbookController {
	private final WordbookService wordbookService;
	

	
}
