package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.entity.Wordbook;
import com.example.demo.form.WordForm;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.repository.WordbookRepository;

@ExtendWith(MockitoExtension.class)
public class WordServiceImplTest {

	@InjectMocks
	WordServiceImpl wordServiceImpl;

	@Mock
	WordbookRepository wordbookRepository;
	@Mock
	WordRepository wordRepository;
	@Mock
	CategoryRepository categoryRepository;

	Wordbook testWordbook;
	Category testCategory1;
	Category testCategory2;
	WordForm wordForm;
	Word savedWord;
	Word w1;
	Word w2;
	Word w3;
	List<Word> relatedWords;
	List<Word> relatedWords2;

	@BeforeEach
	void setUp() {
		testWordbook = new Wordbook();
		testWordbook.setId(1);
		testWordbook.setName("testWordbook");

		testCategory1 = new Category();
		testCategory1.setId(1);
		testCategory1.setName("category1");

		testCategory2 = new Category();
		testCategory2.setId(2);
		testCategory2.setName("category2");

		//関連語
		w1 = new Word();
		w1.setId(11);
		w1.setWordName("w1");
		w1.setRelatedWords(new ArrayList<Word>());
		w2 = new Word();
		w2.setId(12);
		w2.setWordName("w2");
		w2.setRelatedWords(new ArrayList<Word>());
		relatedWords = new ArrayList<>(List.of(w1, w2));
		//更新処理後の関連語
		w3 = new Word();
		w3.setId(13);
		w3.setWordName("w3");
		w3.setRelatedWords(new ArrayList<Word>());
		relatedWords2 = new ArrayList<>(List.of(w3));

		wordForm = new WordForm();
		wordForm.setWordName("word1");
		wordForm.setContent("content");
		wordForm.setCategoryId(1);
		//wordForm.setCategoryName("category1");
		wordForm.setRelatedWordIds(new ArrayList<Integer>(List.of(w1.getId(), w2.getId())));
		wordForm.setWordbookId(testWordbook.getId());

		savedWord = new Word();
		savedWord.setCategory(testCategory1);
		savedWord.setWordbook(testWordbook);
	}

	@Test
	void testFindByWordName() {
		Word word = new Word();
		word.setWordName("word1");
		doReturn(Optional.of(word)).when(wordRepository).findByWordName("word1");
		Optional<Word> wordOpt = wordServiceImpl.findByWordName("word1");
		assertThat(wordOpt.get().getWordName()).isEqualTo("word1");
	}

	@Test
	void testFindAll() {
		Word word1 = new Word();

		word1.setId(1);
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory1);
		word1.setWordbook(testWordbook);

		Word word2 = new Word();

		word2.setId(2);
		word2.setWordName("word2");
		word2.setContent("content2");
		word2.setCategory(testCategory2);
		word1.setWordbook(testWordbook);

		List<Word> list = new ArrayList<>(List.of(word1, word2));
		doReturn(list).when(wordRepository).findAll();

		List<Word> resultList = wordServiceImpl.findAll();
		assertThat(resultList).hasSize(2);
		assertThat(resultList.get(0).getWordName()).isEqualTo("word1");
		assertThat(resultList.get(1).getWordName()).isEqualTo("word2");
	}

	@Test
	void testFindById() {
		Word word1 = new Word();
		word1.setId(1);
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory1);
		doReturn(Optional.of(word1)).when(wordRepository).findById(1);

		Optional<Word> wordOpt = wordServiceImpl.findById(1);
		assertThat(wordOpt.get().getWordName()).isEqualTo("word1");
	}

	@Test
	//新規登録
	void testAddWord() {
		doReturn(Optional.of(testCategory1)).when(categoryRepository).findById(1);
		doReturn(Optional.of(testWordbook)).when(wordbookRepository).findById(1);
		doReturn(savedWord).when(wordRepository).save(any());
		doReturn(Optional.of(w1)).when(wordRepository).findById(11);
		doReturn(Optional.of(w2)).when(wordRepository).findById(12);

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
	//更新
	void testUpdateWord() {
		Word word = new Word();
		word.setId(1);
		word.setWordName("word");
		word.setCategory(testCategory1);
		word.setRelatedWords(relatedWords);
		doReturn(Optional.of(word)).when(wordRepository).findById(1);

		wordForm.setWordName("newWordName");
		wordForm.setContent("newContent");
		wordForm.setCategoryId(2);
		wordForm.setRelatedWordIds(List.of(13));
		doReturn(Optional.of(testCategory2)).when(categoryRepository).findById(wordForm.getCategoryId());
		doReturn(Optional.of(w3)).when(wordRepository).findById(13);

		Word savedWord = new Word();
		savedWord.setWordName("newWordName");
		savedWord.setCategory(testCategory2);
		savedWord.setContent("newContent");
		savedWord.setRelatedWords(relatedWords2);

		doReturn(savedWord).when(wordRepository).save(any());
		wordServiceImpl.updateWord(1, wordForm);
		assertThat(word.getWordName()).isEqualTo("newWordName");
		assertThat(word.getCategory().getName()).isEqualTo("category2");
		assertThat(word.getRelatedWords().get(0).getWordName()).isEqualTo("w3");
	}

	@Test
	//WordForm -> Word　への変換(正常)
	void testTransferWordFormToWord_success() {
		
		doReturn(Optional.of(testCategory1)).when(categoryRepository).findById(1);
		doReturn(Optional.of(testWordbook)).when(wordbookRepository).findById(1);
		doReturn(Optional.of(w1)).when(wordRepository).findById(11);
		doReturn(Optional.of(w2)).when(wordRepository).findById(12);
				
		Word testWord = new Word();
		wordServiceImpl.transferWordFormToWord(testWord, wordForm);
		
		assertThat(testWord.getWordName()).isEqualTo("word1");
		assertThat(testWord.getContent()).isEqualTo("content");
		assertThat(testWord.getCategory().getId()).isEqualTo(1);
		assertThat(testWord.getRelatedWords()).contains(w1,w2);
	}
	@Test
	//WordForm -> Word　への変換(categoryなし)
	void testTransferWordFormToWord_NotFoundCategory() {
		
		doReturn(Optional.empty()).when(categoryRepository).findById(1);
		doReturn(Optional.of(testWordbook)).when(wordbookRepository).findById(1);
		doReturn(Optional.of(w1)).when(wordRepository).findById(11);
		doReturn(Optional.of(w2)).when(wordRepository).findById(12);
		
		Word testWord = new Word();
		wordServiceImpl.transferWordFormToWord(testWord, wordForm);
		
		assertThat(testWord.getWordName()).isEqualTo("word1");
		assertThat(testWord.getContent()).isEqualTo("content");
		assertThat(testWord.getCategory()).isNull();
		assertThat(testWord.getRelatedWords()).contains(w1,w2);
	}
}
