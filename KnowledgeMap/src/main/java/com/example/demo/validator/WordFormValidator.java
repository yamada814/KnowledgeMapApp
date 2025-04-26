package com.example.demo.validator;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.service.WordService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WordFormValidator implements Validator {
	private final WordService wordService;

	@Override
	public boolean supports(Class<?> clazz) {
		return WordForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		WordForm wordForm = (WordForm) target;
		// 関連語に自身のwordを選択するとエラー
		if (wordForm.getRelatedWordIds() != null) {
			List<String> relatedWordNames = wordForm.getRelatedWordIds().stream()
					.map(relatedWordId -> wordService.findById(relatedWordId))
					.filter(opt -> opt.isPresent())
					.map(opt -> opt.get().getWordName())
					.toList();
			if (relatedWordNames.contains(wordForm.getWordName())) {
				errors.rejectValue("relatedWordIds", null, "自身の単語は関連語として登録できません");
			}
		}
		// 既存wordNameならエラー
		Optional<Word> existingWordOpt = wordService.findByWordName(wordForm.getWordName());
		if (existingWordOpt.isPresent()) {
			Word existingWord = existingWordOpt.get();
			//   wordNameによる検索で既存だった  かつ  新規登録の時  
			//					または  
			//   wordNameによる検索で既存だった  かつ  既存のwordを編集の時
			//   ( 編集対象のwordは wordNameによる検索で見つかったword と同じidである必要がある)
			if (wordForm.getId() == null || !wordForm.getId().equals(existingWord.getId())) {
//				errors.rejectValue("wordName", null, wordForm.getWordName() + "は既に登録されています");
				errors.rejectValue("wordName", null, "はすでに登録されています");
			}
		}
		//categoryIdとcategoryNameの両方に入力があればエラー
		if(wordForm.getCategoryId() != null && wordForm.getCategoryName() != null && !wordForm.getCategoryName().isBlank()) {
			errors.rejectValue("categoryId", null,"新規カテゴリを入力した場合は、カテゴリを選択できません");
		}
	}
}
