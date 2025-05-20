package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;

import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Sql("ServiceRepositoryIntegrationTest.sql")
@Transactional
public class WordServiceTest {
	@Autowired
	WordService wordService;

	@Test
	//新規追加
	void testAddWord() {
		WordForm wordForm = new WordForm();
		wordForm.setWordName("newWord");
		wordForm.setContent("newContent");
		wordForm.setCategoryId(1);
		wordForm.setWordbookId(1);
		wordForm.setRelatedWordIds(new ArrayList<Integer>(List.of(1)));
		wordService.addWord(wordForm);

		Optional<Word> wordOpt = wordService.findByWordName("newWord");
		assertThat(wordOpt.get().getWordName()).isEqualTo("newWord");
	}

	@Test
	//更新（正常）
	void testUpdateWord_success() {
		WordForm wordForm = new WordForm();
		wordForm.setWordName("word1");
		wordForm.setContent("newContent");
		wordForm.setCategoryId(1);
		wordForm.setRelatedWordIds(new ArrayList<Integer>(List.of(2)));
		wordForm.setWordbookId(1);

		wordService.updateWord(1, wordForm);

		Optional<Word> wordOpt = wordService.findByWordName("word1");
		assertThat(wordOpt.get().getContent()).isEqualTo("newContent");
		assertThat(wordOpt.get().getRelatedWords().get(0).getWordName()).isEqualTo("word2");

		//関連語として指定された側（word.id=2）でも、
		//関連語として更新処理した側（word.id=1）が登録されていることを確認
		assertThat(wordService.findById(2).getRelatedWords().get(0).getId()).isEqualTo(1);
	}

	@Test
	//更新（自身のwordを関連語として指定した時）
	void testUpdateWord_registMySelfAsRelatedWord() {
		WordForm wordForm = new WordForm();
		wordForm.setWordName("word1");
		wordForm.setContent("newContent");
		wordForm.setCategoryId(1);
		wordForm.setWordbookId(1);

		// 関連語に自身のwordを指定
		wordForm.setRelatedWordIds(new ArrayList<Integer>(List.of(1)));//関連wordId = 1
		wordService.updateWord(1, wordForm);//更新処理対象wordId = 1

		Optional<Word> WordOpt = wordService.findByWordName("word1");
		assertThat(WordOpt.get().getContent()).isEqualTo("newContent");
		assertThat(WordOpt.get().getRelatedWords().size()).isEqualTo(0);

		assertThat(wordService.findById(1).getRelatedWords().size()).isEqualTo(0);
	}
}
