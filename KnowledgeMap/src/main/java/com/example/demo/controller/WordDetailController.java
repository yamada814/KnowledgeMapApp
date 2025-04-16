package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("wordDetail")
public class WordDetailController {
	private final WordService wordService;
	private final CategoryService categoryService;
	
	//wordFormのrelatedWordIds(List<Integer>)からrelatedWordNames(List<String>)へ変換するメソッド
	public List<String> getRelatedWordNames(WordForm wordForm){
		if (wordForm.getRelatedWordIds() == null) return List.of();
		return wordForm.getRelatedWordIds().stream()
				.map(relatedWordId -> wordService.findById(relatedWordId))
				.filter(opt -> opt.isPresent())
				.map(opt -> opt.get().getWordName())
				.toList();
	}

	//word詳細画面へ
	@GetMapping("/{id}")
	public String showDetail(
			@PathVariable("id") Integer id,
			Model model) {
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
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
			RedirectAttributes redirectAttributes) {
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("delete_error", "指定されたwordは存在しません");
			redirectAttributes.addFlashAttribute("wordList", wordService.findAll());
			return "redirect:/wordList";
		}
		wordService.deleteById(id);
		redirectAttributes.addFlashAttribute("wordList", wordService.findAll());
		redirectAttributes.addFlashAttribute("delete_ok", "wordを削除しました");
		return "redirect:/wordList";
	}

	//word編集画面
	@GetMapping("/{id}/editForm")
	public String showEditForm(
			@PathVariable("id") Integer id,
			//新規登録の確認画面からの遷移であるフラグ
			@RequestParam(name = "fromRegistConfirm", required = false) String fromRegistConfirm,
			@ModelAttribute WordForm wordForm,
			Model model) {
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
			return "edit_error";
		}
		Word word = wordOpt.get();
		// 編集用wordFormに、DBから検索したwordの値をセット
		wordForm.setWordName(word.getWordName());
		wordForm.setContent(word.getContent());
		wordForm.setCategoryId(word.getCategory().getId());
		List<Integer> relatedWordIds = word.getRelatedWords().stream()
				.map(relatedWord -> relatedWord.getId())
				.toList();
		wordForm.setRelatedWordIds(relatedWordIds);
		//関連語のWordNameリストをモデル格納
		List<String> relatedWordNames = word.getRelatedWords().stream()
				.map(relatedWord -> relatedWord.getWordName())
				.toList();
		model.addAttribute("relatedWordNames", relatedWordNames);		
		model.addAttribute("categories", categoryService.findAll());//カテゴリ選択用
		model.addAttribute("wordList",wordService.findAll());//関連語選択用
		model.addAttribute("word", word);
		
		//regist_confirmから遷移した場合 戻るボタンの種類を切り替えるためのフラグを用意
		if (fromRegistConfirm != null) {
			model.addAttribute("fromRegistConfirm", true);
		}
		return "edit_form";
	}

	//word編集確認
	@PostMapping("/{id}/editConfirm")
	public String editConfirm(
			@Validated WordForm wordForm,
			BindingResult result,
			RedirectAttributes redirectAttribute,
			Model model,
			@PathVariable("id") Integer id) {
		Optional<Word> wordOpt = wordService.findById(id);
		if (wordOpt.isEmpty()) {
			return "word_detail_error";
		}
		Word word = wordOpt.get();
		//バリデーションチェック
		if (result.hasErrors()) {
			model.addAttribute("relatedWordNames",getRelatedWordNames(wordForm));//relatedWordIdsをrelatedWordNamesに変換したリスト
			model.addAttribute("wordList",wordService.findAll());
			model.addAttribute("word", word);
			model.addAttribute("categories", categoryService.findAll());
			return "edit_form";
		}
		//wordNameがカブっていたら 入力情報をモデルに格納して 入力フォーム画面へ返す
		Optional<Word> existingWordOpt = wordService.findByWordName(wordForm.getWordName());
		if (existingWordOpt.isPresent() && !existingWordOpt.get().getId().equals(id)) {
			model.addAttribute("relatedWordNames",getRelatedWordNames(wordForm));
			model.addAttribute("wordList",wordService.findAll());
			model.addAttribute("word", word);
			model.addAttribute("categories", categoryService.findAll());
			
			model.addAttribute("word_duplicate", existingWordOpt.get().getWordName() + "は既に登録済です");
			return "edit_form";
		}
		//categoryNameに入力があれば そのcategoryNameで登録済みかチェックし なければ新規登録
		String newCategoryName = wordForm.getCategoryName();
		if (newCategoryName != null && !newCategoryName.isBlank()) {
			Optional<Category> categoryOpt = categoryService.searchByName(newCategoryName);
			// catgoryNameが未登録 -> category新規登録
			if (categoryOpt.isEmpty()) {
				Category newCategory = categoryService.addCategory(newCategoryName);
				wordForm.setCategoryId(newCategory.getId());
			// categoryNameが登録済み -> 登録済みのcategoryNameからcategoryIdを取得してフォームにセット
			} else { 
				wordForm.setCategoryId(categoryOpt.get().getId());
			}
		}
		//		System.out.println("■ ■ ■ ■ ■ wordForm.categoryId:" + wordForm.getCategoryId());
		model.addAttribute("relatedWordNames",getRelatedWordNames(wordForm));
		model.addAttribute("word", word);
		return "edit_confirm";
	}
	@PostMapping("/{id}/edit")
	public String edit(WordForm wordForm,
			@PathVariable("id") Integer id,
			RedirectAttributes redirectAttribute) {
		wordService.updateWord(id, wordForm);
		redirectAttribute.addFlashAttribute("edit_ok", "編集が完了しました");
		redirectAttribute.addFlashAttribute("wordList", wordService.findAll());
		return "redirect:/wordList";
	}
}
