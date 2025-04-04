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
			Model model){
		wordService.deleteById(id);
		model.addAttribute("wordList", wordService.findAll());
		model.addAttribute("delete_ok", "wordを削除しました");
		return "word_list";	
	}
	
	//word編集画面
	@PostMapping("/{id}/editForm")
	public String editWord(
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
		wordForm.setCategoryName(word.getCategory().getName());	
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
			Model model,
			@PathVariable("id") Integer id) {
		if(result.hasErrors()) {
			Optional<Word> wordOpt =  wordService.findById(id);
			Word word = wordOpt.get();
			model.addAttribute("word", word);
			model.addAttribute("categories", categoryService.findAll());
			return "edit_word";
		}
		wordService.updateWord(id,wordForm);
		model.addAttribute("edit_ok","編集が完了しました");
		model.addAttribute("wordList",wordService.findAll());
		return "word_list";
	}
}
