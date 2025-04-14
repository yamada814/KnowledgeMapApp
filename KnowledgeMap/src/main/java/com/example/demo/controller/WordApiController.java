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

import com.example.demo.entity.Word;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class WordApiController {
	private final WordService wordService;
	private final CategoryService categoryService;
	@GetMapping("/words")
	public List<Word> getWordsByCategoryId(@RequestParam("categoryId") Integer categoryId){
		return wordService.findByCategoryId(categoryId);
	}
	@DeleteMapping("/categories/{id}")
	@ResponseBody
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
	    categoryService.deleteByCategoryId(id);
	    return ResponseEntity.ok().build();
	}

}
