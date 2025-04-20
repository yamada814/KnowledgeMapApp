package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;
import com.example.demo.validator.WordFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WordController {
	private final WordService wordService;
	private final CategoryService categoryService;
	private final WordFormValidator wordFormValidator;
	
	@InitBinder("wordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(wordFormValidator);
	}
	// word一覧表示
	@GetMapping("/wordList")
	public String showWordList(Model model) {
		model.addAttribute("categories", categoryService.findAll());
		return "word_list";
	}
	
	//新規登録画面
	@GetMapping("/showWordForm")
	public String showWordForm(Model model) {
		model.addAttribute("wordForm", new WordForm());
		model.addAttribute("wordList", wordService.findAll());
		model.addAttribute("categories", categoryService.findAll());
		return "regist_form";
	}

	// 登録画面へ戻るとき
	@PostMapping("showWordForm")
	public String backToWordForm(@ModelAttribute("wordForm") WordForm wordForm, Model model) {
		model.addAttribute("wordList", wordService.findAll());
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
			model.addAttribute("wordList", wordService.findAll());
			// 既存word情報をmodelに格納
			Optional<Word> wordOpt = wordService.findByWordName(wordForm.getWordName());
			if (wordOpt.isPresent()) {
				model.addAttribute("existingWord", wordOpt.get());
			}
			return "regist_form";
		}
		// categoryNameに入力があった場合
		String newCategoryName = wordForm.getCategoryName();
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			Optional<Category> categoryOpt =  categoryService.findByName(newCategoryName);
			if (categoryOpt.isEmpty()) { // 入力されたcategoryNameが未登録 -> categoryテーブルへ新規登録
				Category newCategory = categoryService.addCategory(newCategoryName);
				wordForm.setCategoryId(newCategory.getId());
			} else { // 登録済 -> 登録済のcategoryNameからcategoryIdを取得してフォームにセット
				wordForm.setCategoryId(categoryOpt.get().getId());
			}
		}
		// categoryIdからcategoryNameをセット
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		wordForm.setCategoryName(categoryOpt.get().getName());
		model.addAttribute("relatedWordNames",wordService.getRelatedWordNames(wordForm));
		return "regist_confirm";
	}
	//DB新規登録
	@PostMapping("/regist")
	public String regist(WordForm wordForm,RedirectAttributes redirectAttribute) {
		 // 存在しないカテゴリの場合エラー 
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		wordService.addWord(wordForm);
		redirectAttribute.addFlashAttribute("regist_ok", "登録完了しました");
		redirectAttribute.addFlashAttribute("wordList", wordService.findAll());
		return "redirect:/wordList";
	}
}
