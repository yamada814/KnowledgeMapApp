package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;

import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;

@SpringBootTest(webEnvironment=WebEnvironment.NONE)
@Sql("ServiceRepositoryIntegrationTest.sql")
@Transactional
public class WordServiceTest {
	@Autowired
	WordService wordService;
	
	@Test
	void testAddWord() {
		WordForm wordForm = new WordForm();
		wordForm.setWordName("word2");
		wordForm.setContent("content2");
		wordForm.setCategoryId(1);
		wordService.addWord(wordForm);
		
		Optional<Word> wordOpt = wordService.findByWordName("word2");
		assertThat(wordOpt.get().getWordName()).isEqualTo("word2");		
	}
}
