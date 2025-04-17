package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	// word一覧表示
	@GetMapping("/wordList")
	public String showWordList(Model model) {
		model.addAttribute("categories", categoryService.findAll());
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
			return "regist_form";
		}
		// categoryNameに入力があった場合
		String newCategoryName = wordForm.getCategoryName();
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			// 入力されたcatgoryNameが登録済みではないか
			Optional<Category> categoryOpt =  categoryService.searchByName(newCategoryName);
			if (categoryOpt.isEmpty()) { // 登録済みではない -> categoryテーブルへ新規登録
				Category newCategory = categoryService.addCategory(newCategoryName);
				wordForm.setCategoryId(newCategory.getId());
			} else { // 登録済み -> 登録済みのcategoryNameからcategoryIdを取得してフォームにセット
				wordForm.setCategoryId(categoryOpt.get().getId());
			}
		}
		// categoryIdからcategoryNameをセット
		Optional<Category> categoryOpt = categoryService.findByCategoryId(wordForm.getCategoryId());
		if (categoryOpt.isEmpty()) {
			return "regist_error";
		}
		wordForm.setCategoryName(categoryOpt.get().getName());
		
		List<Integer> relatedWordIds = wordForm.getRelatedWordIds();
		List<String> relatedWordNames = relatedWordIds.stream()
				.map(id -> wordService.findById(id))
				.filter(wordOpt -> wordOpt.isPresent())
				.map(wordOpt -> wordOpt.get().getWordName())
				.toList();
		model.addAttribute("relatedWordNames",relatedWordNames);
		//登録しようとしてるwordがすでに存在しているか確認
		Optional<Word> wordOpt = wordService.findByWordName(wordForm.getWordName());
		if (wordOpt.isPresent()) {
			model.addAttribute("word", wordOpt.get());
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("exists", true);
			return "regist_confirm";
		}
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
	public String regist(WordForm wordForm,RedirectAttributes redirectAttribute) {
		//categoryのチェック
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
