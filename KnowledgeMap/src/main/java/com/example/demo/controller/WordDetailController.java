package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.Word;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("wordDetail")
public class WordDetailController {
	private final WordService wordService;
	private final CategoryService categoryService;
	
	//word詳細へ
	@GetMapping("/{id}")
	public String showDetail(
			@PathVariable("id") Integer id,
			Model model){
		Optional<Word> wordOpt = wordService.findById(id);
		if(wordOpt.isEmpty()) {
			return "word_detail_error";
		}
//		System.out.println("■ ■ ■ ■ ■ ■ ");
//		System.out.println(wordOpt.get().getName());
		model.addAttribute("word", wordOpt.get());
		return "word_detail";
	}
	//word削除
	@PostMapping("/{id}/delete")
	public String deleteWord(
			@PathVariable("id") Integer id,
			Model model){
		wordService.deleteById(id);
		model.addAttribute("wordList", wordService.findAll());
		model.addAttribute("delete_ok", "wordを削除しました");
		return "word_list";	
	}
	

}
