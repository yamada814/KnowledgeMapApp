package com.example.demo.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.demo.entity.Wordbook;
import com.example.demo.form.WordbookForm;
import com.example.demo.service.WordbookService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WordbookValidator implements Validator{
	private final WordbookService wordbookService;

	@Override
	public boolean supports(Class<?> clazz) {
		return WordbookForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		WordbookForm wordbookForm = (WordbookForm) target;
		Optional<Wordbook> wordbookOpt =  
				wordbookService.findByWordbookNameAndUserId(wordbookForm.getWordbookName(),wordbookForm.getUserId());
		if(wordbookOpt.isPresent()) {
			errors.rejectValue("wordbookName", null, "この単語帳名は既に登録されています");
		}
	}
}
