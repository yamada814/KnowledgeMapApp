package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WordDetailDto;
import com.example.demo.dto.WordDto;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class WordApiController {
	
	private final WordService wordService;
	private final CategoryService categoryService;
	
	// 単語一覧を表示
	@GetMapping("/words")
	public List<WordDto> getWordsByCategoryId(@RequestParam("categoryId") Integer categoryId){
		return wordService.findWordsByCategoryId(categoryId);
	}

	
	// wordDetail表示
	@GetMapping("/words/{id}")
	public ResponseEntity<WordDetailDto> getDetail(@PathVariable("id") Integer id) {
		WordDetailDto dto = wordService.findWordDetailDtoById(id);
		if(dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}
	
	
	// カテゴリ削除
	@DeleteMapping("/categories/{id}")
	@ResponseBody
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
	    categoryService.deleteByCategoryId(id);
	    return ResponseEntity.ok().build();
	}
	
	// 単語削除
	@DeleteMapping("/words/{id}")
	public ResponseEntity<Void> deleteWord(@PathVariable("id") Integer id) {
	    boolean deleted = wordService.deleteById(id);
	    if (deleted) {
	        return ResponseEntity.ok().build();
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
}
