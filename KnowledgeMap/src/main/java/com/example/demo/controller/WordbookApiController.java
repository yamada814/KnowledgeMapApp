package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.Wordbook;
import com.example.demo.form.WordbookForm;
import com.example.demo.service.LoginUserDetails;
import com.example.demo.service.WordbookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wordbooks/api")
@RequiredArgsConstructor
public class WordbookApiController {
	
	private final WordbookService wordbookService;
	
	@PostMapping("/regist")
	public ResponseEntity<WordbookDto> registWordBook(
			@AuthenticationPrincipal LoginUserDetails loginUserDetails,
			WordbookForm wordbookForm
			){
		//Wordbook型に変換
		Wordbook wordbook = new Wordbook();
		wordbook.setUser(loginUserDetails.getUser());
		wordbook.setName(wordbookForm.getWordbookName());
		//登録
		WordbookDto dto = wordbookService.save(wordbook);
		return ResponseEntity.ok(dto);	
	}

	
}
