package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.Wordbook;
import com.example.demo.form.WordbookForm;
import com.example.demo.service.LoginUserDetails;
import com.example.demo.service.WordbookService;
import com.example.demo.validator.WordbookValidator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wordbooks/api")
@RequiredArgsConstructor
public class WordbookApiController {

	private final WordbookService wordbookService;
	private final WordbookValidator wordbookValidator;
	
	@InitBinder("wordbookForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(wordbookValidator);
	}

	@PostMapping("/regist")
	public ResponseEntity<?> registWordBook(
			@AuthenticationPrincipal LoginUserDetails loginUserDetails,
			@Validated WordbookForm wordbookForm,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
	        // エラーメッセージのリストを作成
	        List<String> errorList = bindingResult.getAllErrors().stream()
	            .map(error -> error.getDefaultMessage())
	            .collect(Collectors.toList());
	        // 400 Bad Request でエラーリストを返す
	        return ResponseEntity.badRequest().body(errorList);
	    }
		//登録処理
		Wordbook wordbook = new Wordbook();
		wordbook.setUser(loginUserDetails.getUser());
		wordbook.setName(wordbookForm.getWordbookName());
		WordbookDto dto = wordbookService.save(wordbook);
		return ResponseEntity.ok(dto);
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Integer id){
		boolean deleted = wordbookService.deleteById(id);
		if (deleted){
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error","指定された単語張は存在しません"));
	}

}
