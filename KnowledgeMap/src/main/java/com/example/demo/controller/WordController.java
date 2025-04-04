package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WordController {
	private final WordService wordService;
	private final CategoryService categoryService;
	
	//test
	
	@GetMapping("/wordList")
	public String showWordList(Model model) {
		model.addAttribute("wordList", wordService.findAll());
		return "word_list";
	}
//	@GetMapping("/wordDetail/{name}")
//	public @ResponseBody String showDetail(
//			@PathVariable String name,
//			@RequestParam int id,
//			Model model){
//		return "リクエストを正常に受け付けました" + name + id;
//	}
	@GetMapping("/wordDetail/{name}")
	public String showDetail(
			@PathVariable String name,
			@RequestParam Long id,
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


	
	@GetMapping("/showWordForm")
	public String showWordForm(Model model) {
		model.addAttribute("wordForm", new WordForm());
		model.addAttribute("categories", categoryService.findAll());
		return "regist_form";
	}
	@PostMapping("/registConfirm")
	public String registConfirm(
			@Validated WordForm wordForm,
			BindingResult result,
			Model model) {
		if(result.hasErrors()) {
			model.addAttribute("categories", categoryService.findAll());
			return "regist_form";
		}
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if(categoryOpt.isEmpty()) {
			return "regist_error";
		}
		wordForm.setCategoryName(categoryOpt.get().getName());		
		return "regist_confirm";
	}
	@PostMapping("/regist")
	public String regist(WordForm wordForm,Model model) {
		System.out.println("受け取ったcategoryId = " + wordForm.getCategoryId());
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if(categoryOpt.isEmpty()) {
			return "regist_error";
		}	
		wordService.addWord(wordForm);
		model.addAttribute("regist_ok", "登録完了しました");
		model.addAttribute("wordList", wordService.findAll());
		return "word_list";
	}


}
