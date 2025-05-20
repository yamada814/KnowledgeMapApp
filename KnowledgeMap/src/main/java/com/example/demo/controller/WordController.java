package com.example.demo.controller;

import java.util.List;
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
	
	@ModelAttribute("categoryList")
	public List<Category> setCategoryOptions(@PathVariable("wordbookId") Integer wordbookId) {
		return categoryService.findByWordbookId(wordbookId);
	}

	@ModelAttribute("wordList")
	public List<Word> setRelatedWordOptions(@PathVariable("wordbookId") Integer wordbookId) {
		return wordService.findByWordbookId(wordbookId);
	}

	// word一覧表示
	@GetMapping("")
	public String showWordList(Model model, @PathVariable("wordbookId") Integer wordbookId) {
		model.addAttribute("wordbookId", wordbookId);
		return "word_list";
	}

	//新規登録画面
	@GetMapping("/showWordForm")
	public String showWordForm(Model model, @PathVariable("wordbookId") Integer wordbookId) {
		WordForm wordForm = new WordForm();
		wordForm.setWordbookId(wordbookId);
		model.addAttribute("wordForm", wordForm);
		return "regist_form";
	}

	// 登録画面へ戻るとき
	@PostMapping("/showWordForm")
	public String backToWordForm(
			@ModelAttribute("wordForm") WordForm wordForm,
			@PathVariable("wordbookId") Integer wordbookId,
			Model model) {
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
			// 既存word情報をmodelに格納
			wordService.findByWordNameAndWordbookId(wordForm.getWordName(), wordForm.getWordbookId())
					.ifPresent(existingWord -> model.addAttribute("existingWord", existingWord));
			return "regist_form";
		}
		String newCategoryName = wordForm.getCategoryName();
		// categoryNameに入力があった場合
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			Optional<Category> categoryOpt = categoryService.findByNameAndWordbookId(newCategoryName, wordbookId);

			// 入力されたcategoryNameが未登録 -> categoryテーブルへ新規登録 -> 登録したcategoryIdをセット
			if (categoryOpt.isEmpty()) {
				Category registedCategory = categoryService.addCategory(newCategoryName, wordbookId);
				wordForm.setCategoryId(registedCategory.getId());

			// 入力されたcategoryNameが既存 -> 既存のcategoryIdをセット
			} else {
				wordForm.setCategoryId(categoryOpt.get().getId());
			}
		}
		// categoryIdからcategoryNameをセット
		Category category = categoryService.findByCategoryId(wordForm.getCategoryId());
		wordForm.setCategoryName(category.getName());

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
		Word registedWord = wordService.addWord(wordForm);

		redirectAttribute.addFlashAttribute("regist_ok", "登録しました");
		redirectAttribute.addFlashAttribute("wordList", wordService.findByWordbookId(wordbookId));

		return String.format("redirect:/wordbooks/%d/words?categoryId=%d&id=%d",
				wordbookId, registedWord.getCategory().getId(),
				registedWord.getId());
	}
}
