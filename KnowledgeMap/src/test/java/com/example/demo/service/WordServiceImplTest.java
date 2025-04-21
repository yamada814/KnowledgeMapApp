package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.form.WordForm;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRepository;

@ExtendWith(MockitoExtension.class)
public class WordServiceImplTest {

	@InjectMocks
	WordServiceImpl wordServiceImpl;
	
	@Mock
	WordRepository wordRepository;
	
	@Mock
	CategoryRepository categoryRepository;
	
	@Test
	void testFindByWordName() {		
		Word word = new Word();
		word.setWordName("word1");		
		String name = "word1";
		doReturn(Optional.of(word)).when(wordRepository).findByWordName(name);
		Optional<Word> wordOpt =  wordServiceImpl.findByWordName(name);
		assertThat(wordOpt.get().getWordName()).isEqualTo("word1");
	}
	@Test
	void testAddWord() {
		Category category = new Category();
		category.setId(1);
		category.setName("category1");
		doReturn(Optional.of(category)).when(categoryRepository).findById(1);
			
		WordForm wordForm = new WordForm();
		wordForm.setWordName("word1");
		wordForm.setContent("content");
		wordForm.setCategoryId(1);
		wordForm.setCategoryName("category1");
		wordServiceImpl.addWord(wordForm);
		//Mockのsaveメソッドに渡す引数を参照できるようにする
		ArgumentCaptor<Word> captor = ArgumentCaptor.forClass(Word.class);
		verify(wordRepository).save(captor.capture());
		
		Word savedWord = captor.getValue();
		assertThat(savedWord.getWordName()).isEqualTo("word1");
		assertThat(savedWord.getContent()).isEqualTo("content");
		assertThat(savedWord.getCategory().getId()).isEqualTo(1);
		assertThat(savedWord.getCategory().getName()).isEqualTo("category1");	
	}
	@Test
	void testFindAll() {
		Word word1 = new Word();
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		word1.setId(1);
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(category1);
		
		Word word2 = new Word();
		Category category2 = new Category();
		category2.setId(2);
		category2.setName("category2");
		word2.setId(2);
		word2.setWordName("word2");
		word2.setContent("content2");
		word2.setCategory(category2);
		
		List<Word> list = new ArrayList<Word>(List.of(word1,word2));
		doReturn(list).when(wordRepository).findAll();
		
		List<Word> resultList =  wordServiceImpl.findAll();
		assertThat(resultList).hasSize(2);
		assertThat(resultList.get(0).getWordName()).isEqualTo("word1");
		assertThat(resultList.get(1).getWordName()).isEqualTo("word2");	
	}
	@Test
	void testFindById() {
		Integer id = 1;
		Word word1 = new Word();
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		word1.setId(1);
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(category1);		
		doReturn(Optional.of(word1)).when(wordRepository).findById(id);
		
		Optional<Word> wordOpt = wordServiceImpl.findById(id);
		assertThat(wordOpt.get().getWordName()).isEqualTo("word1");	
	}

	@Test
	void testUpdateWord() {
		Integer id = 1;
		Category oldCategory = new Category();
		oldCategory.setId(1);
		oldCategory.setName("oldCategory");
		Word word = new Word();
		word.setId(id);
		word.setWordName("word");
		word.setCategory(oldCategory);
		
		WordForm wordForm = new WordForm();
		wordForm.setWordName("newWordName");
		wordForm.setContent("newContent");
		wordForm.setCategoryId(2);
		
		Category newCategory = new Category();
		newCategory.setId(2);
		newCategory.setName("newCategory");
		
		doReturn(Optional.of(word)).when(wordRepository).findById(id);
		doReturn(Optional.of(newCategory)).when(categoryRepository).findById(2);
		
		wordServiceImpl.updateWord(1,wordForm);	
		assertThat(word.getWordName()).isEqualTo("newWordName");
		assertThat(word.getCategory().getName()).isEqualTo("newCategory");		
	}
	
}
