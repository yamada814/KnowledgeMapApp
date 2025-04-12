package com.example.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.CategoryService;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("wordDetail")
public class WordDetailController {
	private final WordService wordService;
	private final CategoryService categoryService;
	
	//word詳細画面へ
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
			RedirectAttributes redirectAttributes){
		Optional<Word> wordOpt = wordService.findById(id);
		if(wordOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("delete_error","指定されたwordは存在しません");
			redirectAttributes.addFlashAttribute("wordList",wordService.findAll());
			return "redirect:/wordList";
		}
		wordService.deleteById(id);
		redirectAttributes.addFlashAttribute("wordList", wordService.findAll());
		redirectAttributes.addFlashAttribute("delete_ok", "wordを削除しました");
		return "redirect:/wordList";	
	}
	
	//word編集画面
	@GetMapping("/{id}/editForm")
	public String editWordForm(
			@PathVariable("id") Integer id,
			Model model){
		Optional<Word> wordOpt =  wordService.findById(id);
		if(wordOpt.isEmpty()) {
			return "edit_error";
		}
		Word word = wordOpt.get();
		// 編集用wordFormに、DBから検索したwordの値をセット
		WordForm wordForm = new WordForm();
		wordForm.setWordName(word.getWordName());
		wordForm.setContent(word.getContent());
		wordForm.setCategoryId(word.getCategory().getId());
		model.addAttribute("wordForm", wordForm);
		//category選択肢一覧をセット
		model.addAttribute("categories", categoryService.findAll());
		//編集用にwordもセット
		model.addAttribute("word", word);	
		return "edit_word";	
	}
	//word編集
	@PostMapping("/{id}/edit")
	public String editWord(
			@Validated WordForm wordForm,
			BindingResult result,
			RedirectAttributes redirectAttribute,
			Model model,
			@PathVariable("id") Integer id) {
		Optional<Word> wordOpt = wordService.findById(id);
		if(wordOpt.isEmpty()) {
			return "word_detail_error";
		}
		Word word = wordOpt.get();
		if(result.hasErrors()) {
			model.addAttribute("word", word);
			model.addAttribute("categories", categoryService.findAll());
			return "edit_word";
		}
		//wordNameのカブリチェック
		Optional<Word> existingWordOpt = wordService.findByWordName(wordForm.getWordName());
		if(existingWordOpt.isPresent() && !existingWordOpt.get().getId().equals(id)) {
			wordForm.setWordName(word.getWordName());
			model.addAttribute("word", word);
			model.addAttribute("categories", categoryService.findAll());
			model.addAttribute("word_duplicate",existingWordOpt.get().getWordName() + "は既に登録済です");
			return "edit_word";
		}
		//categoryNameに入力がある場合
		String newCategoryName = wordForm.getCategoryName();
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			// 入力されたcatgoryNameが登録済みか
			Optional<Category> categoryOpt =  categoryService.searchByName(newCategoryName);
			if (categoryOpt.isEmpty()) { // 登録済みではない -> categoryテーブルへ新規追加
				Category newCategory = categoryService.addCategory(newCategoryName);
				wordForm.setCategoryId(newCategory.getId());
			} else { // 登録済み -> 登録済みのcategoryNameからcategoryIdを取得してフォームにセット
				wordForm.setCategoryId(categoryOpt.get().getId());
			}
		}
//		System.out.println("■ ■ ■ ■ ■ wordForm.categoryId:" + wordForm.getCategoryId());	
		wordService.updateWord(id,wordForm);
		redirectAttribute.addFlashAttribute("edit_ok","編集が完了しました");
		redirectAttribute.addFlashAttribute("wordList",wordService.findAll());
		return "redirect:/wordList";
	}
}
