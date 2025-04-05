package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

	//word一覧表示
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

	//新規登録画面
	@GetMapping("/showWordForm")
	public String showWordForm(Model model) {
		model.addAttribute("wordForm", new WordForm());
		model.addAttribute("categories", categoryService.findAll());
		return "regist_form";
	}

	//登録内容確認
	@PostMapping("/registConfirm")
	public String registConfirm(
			@Validated WordForm wordForm,
			BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryService.findAll());
			return "regist_form";
		}
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		wordForm.setCategoryName(categoryOpt.get().getName());
		return "regist_confirm";
	}
	@GetMapping("/registCancel")
	public String registCancel(Model model) {
		model.addAttribute("regist_cancel", "登録がキャンセルされました");
		model.addAttribute("wordList", wordService.findAll());
		return "word_list";
	}

	//DB新規登録
	@PostMapping("/regist")
	public String regist(WordForm wordForm, Model model) {
//		System.out.println("受け取ったcategoryId = " + wordForm.getCategoryId());
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		Optional<Word> wordOpt = wordService.findByWordName(wordForm.getWordName());
		if (wordOpt.isPresent()) {
			model.addAttribute("word",wordOpt.get());
			model.addAttribute("categories", categoryService.findAll());
			return "regist_cancel_or_edit";
		}
		wordService.addWord(wordForm);
		model.addAttribute("regist_ok", "登録完了しました");
		model.addAttribute("wordList", wordService.findAll());
		return "word_list";
	}

}
