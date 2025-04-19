package com.example.demo.validator;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
		List<String> relatedWordNames = wordForm.getRelatedWordIds().stream()
				.map(relatedWordId -> wordService.findById(relatedWordId))
				.filter(opt -> opt.isPresent())
				.map(opt -> opt.get().getWordName())
				.toList();
		if(relatedWordNames.contains(wordForm.getWordName())) {
			errors.rejectValue("relatedWordIds",null,"自身のwordは関連語として登録できません");
		}
	}
}
