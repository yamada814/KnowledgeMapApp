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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;
import com.example.demo.validator.WordFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/wordbooks/{wordbookId}/words")
@RequiredArgsConstructor
public class WordController {
	private final WordService wordService;
	private final CategoryService categoryService;
	private final WordFormValidator wordFormValidator;

	@InitBinder("wordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(wordFormValidator);
	}


	//新規登録画面
	@GetMapping("/showWordForm")
	public String showWordForm(Model model,@PathVariable("wordbookId") Integer wordbookId) {
		WordForm wordForm = new WordForm();
		wordForm.setWordbookId(wordbookId);
		model.addAttribute("wordForm", wordForm);
		model.addAttribute("wordList", wordService.findByWordbookId(wordbookId));
		model.addAttribute("categories", categoryService.findByWordbookId(wordbookId));
		return "regist_form";
	}

	// 登録画面へ戻るとき
	@PostMapping("/showWordForm")
	public String backToWordForm(
			@ModelAttribute("wordForm") WordForm wordForm,
			@PathVariable("wordbookId") Integer wordbookId,
			Model model) {
		model.addAttribute("wordList", wordService.findByWordbookId(wordbookId));
		model.addAttribute("categories", categoryService.findByWordbookId(wordbookId));
		return "regist_form";
	}

	//登録内容確認
	@PostMapping("/registConfirm")
	public String registConfirm(
			@ModelAttribute("wordForm") @Validated WordForm wordForm,
			BindingResult result,
			@PathVariable("wordbookId") Integer wordbookId,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryService.findByWordbookId(wordbookId));
			model.addAttribute("wordList", wordService.findByWordbookId(wordbookId));
			// 既存word情報をmodelに格納
			Optional<Word> wordOpt = wordService.findByWordNameAndWordbookId(wordForm.getWordName(),wordForm.getWordbookId());
			if (wordOpt.isPresent()) {
				model.addAttribute("existingWord", wordOpt.get());
			}
			return "regist_form";
		}
		// categoryNameに入力があった場合
		String newCategoryName = wordForm.getCategoryName();
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			Optional<Category> categoryOpt = categoryService.findByNameAndWordbookId(newCategoryName,wordbookId);
			if (categoryOpt.isEmpty()) { // 入力されたcategoryNameが未登録 -> categoryテーブルへ新規登録
				try {
					Category registedCategory = categoryService.addCategory(newCategoryName,wordbookId);
					wordForm.setCategoryId(registedCategory.getId());					
				}catch(IllegalArgumentException e) {
					//System.err.println("カテゴリ追加エラー: " + e.getMessage());
					model.addAttribute("category_add_error", "カテゴリの追加に失敗しました");
					model.addAttribute("categories", categoryService.findByWordbookId(wordbookId));
					model.addAttribute("wordList", wordService.findByWordbookId(wordbookId));
					model.addAttribute("relatedWordNames", wordService.getRelatedWordNames(wordForm));
					model.addAttribute("word", wordService.findById(wordForm.getId()).orElse(null));
					return "regist_form"; // 必要に応じてエラー画面にしてもOK
				}
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
		//relatedWordNames
		if (wordForm.getRelatedWordIds() != null) {
			model.addAttribute("relatedWordNames", wordService.getRelatedWordNames(wordForm));
		}
		return "regist_confirm";
	}

	//DB新規登録
	@PostMapping("/regist")
	public String regist(WordForm wordForm,
			@PathVariable("wordbookId") Integer wordbookId,
			RedirectAttributes redirectAttribute) {
		
		// 存在しないカテゴリの場合エラー
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		Word registedWord = wordService.addWord(wordForm);
		redirectAttribute.addFlashAttribute("regist_ok", "登録しました");
		redirectAttribute.addFlashAttribute("wordList", wordService.findByWordbookId(wordbookId));
		return String.format("redirect:/wordbooks/%d/words?categoryId=%d&id=%d", wordbookId,registedWord.getCategory().getId(),
				registedWord.getId());
	}
}
