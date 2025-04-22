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
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("words")
public class WordDetailController {
	private final WordService wordService;
	private final CategoryService categoryService;
	private final WordFormValidator wordFormValidator;

	@InitBinder("wordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(wordFormValidator);
	}

	//word編集画面
	@GetMapping("/{id}/editForm")
	public String showEditForm(
			@PathVariable("id") Integer id,
			//新規登録の確認画面からの遷移であるフラグ
			@RequestParam(name = "fromRegist", required = false) String fromRegist,
			@ModelAttribute WordForm wordForm,
			Model model) {
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
			return "edit_error";
		}
		//新規登録から遷移した場合 戻るボタンの種類を切り替えるためのフラグを用意
		if (fromRegist != null) {
			model.addAttribute("fromRegist", fromRegist);
		}
		Word word = wordOpt.get();
		// 編集用wordFormに、DBから検索したwordの値をセット
		wordForm.setId(word.getId());
		wordForm.setWordName(word.getWordName());
		wordForm.setContent(word.getContent());
		wordForm.setCategoryId(word.getCategory().getId());
		List<Integer> relatedWordIds = word.getRelatedWords().stream()
				.map(relatedWord -> relatedWord.getId())
				.toList();
		wordForm.setRelatedWordIds(relatedWordIds);
		model.addAttribute("relatedWordNames", wordService.getRelatedWordNames(wordForm));
		model.addAttribute("categories", categoryService.findAll());
		model.addAttribute("wordList", wordService.findAll());
		model.addAttribute("word", word);

		return "edit_form";
	}

	//word編集確認
	@PostMapping("/{id}/editConfirm")
	public String editConfirm(
			@Validated WordForm wordForm,
			BindingResult result,
			Model model,
			@PathVariable("id") Integer id,
			@RequestParam(name = "fromRegist", required = false) String fromRegist) {
		//新規登録から遷移した場合 戻るボタンの種類を切り替えるためのフラグを用意
		if (fromRegist != null) {
			model.addAttribute("fromRegist", "fromRegist");
		}
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
			return "edit_error";
		}
		Word word = wordOpt.get();
		//バリデーションチェック
		if (result.hasErrors()) {
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("wordList", wordService.findAll());
			model.addAttribute("relatedWordNames", wordService.getRelatedWordNames(wordForm));
			model.addAttribute("word", word);
			return "edit_form";
		}
		String newCategoryName = wordForm.getCategoryName();
		// categoryNameに入力があった場合
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			Optional<Category> categoryOpt = categoryService.findByName(newCategoryName);
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
			return "edit_error";
		}
		wordForm.setCategoryName(categoryOpt.get().getName());
		model.addAttribute("relatedWordNames", wordService.getRelatedWordNames(wordForm));
		model.addAttribute("word", word);

		return "edit_confirm";
	}

	@PostMapping("/{id}/edit")
	public String edit(@ModelAttribute WordForm wordForm,
			@PathVariable("id") Integer id,
			RedirectAttributes redirectAttribute) {
		Word updatedWord = wordService.updateWord(id, wordForm);
		redirectAttribute.addFlashAttribute("edit_ok", "編集が完了しました");
		redirectAttribute.addFlashAttribute("wordList", wordService.findAll());
		return String.format("redirect:/wordList?categoryId=%d&id=%d", updatedWord.getCategory().getId(),updatedWord.getId());
	}
}
