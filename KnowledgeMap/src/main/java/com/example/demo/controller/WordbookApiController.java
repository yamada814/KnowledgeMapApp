package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	@Autowired
	private final WordbookService wordbookService;
	
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
		//単語帳名の重複チェック
		Optional<Wordbook> wordbookOpt =  
				wordbookService.findByWordbookNameAndUserId(wordbookForm.getWordbookName(),loginUserDetails.getUser().getId());
		if(wordbookOpt.isPresent()) {
			List<String> errorList = new ArrayList<>(List.of("この単語帳名は既に登録されています"));
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
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
		wordbookService.deleteById(id);
		return ResponseEntity.ok().build();
	}


}
