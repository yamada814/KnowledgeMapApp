package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entity.Category;
import com.example.demo.entity.Word;

@DataJpaTest
public class WordRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private WordRepository wordRepository;
	
	//新規登録
	@Test
	void testSave() {
		Category category = new Category();
		category.setName("category1");
		categoryRepository.save(category);
		
		Word word = new Word();
		word.setWordName("word");
		word.setContent("content");
		word.setCategory(category);
		
		Word savedWord = wordRepository.save(word);
		Optional<Word> wordOpt = wordRepository.findById(savedWord.getId());
		Word getWord = wordOpt.get();
		assertThat(getWord.getWordName()).isEqualTo("word");
		assertThat(getWord.getContent()).isEqualTo("content");
		
	}
	
	//wordNameによる検索
	@Test
	void testFindbyWordName() {
		
		Category category1 = new Category();
		category1.setName("カテゴリー1");
		
		Category category2 = new Category();
		category2.setName("カテゴリー2");
		
		categoryRepository.save(category1);
		categoryRepository.save(category2);
		
		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(category1);
		
		Word word2 = new Word();
		word2.setWordName("word2");
		word2.setContent("content2");
		word2.setCategory(category2);
		
		wordRepository.save(word1);
		wordRepository.save(word2);
		
		Optional<Word> wordOpt = wordRepository.findByWordName(word1.getWordName());
		assertThat(wordOpt).isPresent();
		assertThat(wordOpt.get().getContent()).isEqualTo("content1");
		
	}
	
	//categoryIdによる検索
	@Test
	void testFindByCategoryId(){
		Category category1 = new Category();
		category1.setName("カテゴリー1");
		
		Category category2 = new Category();
		category2.setName("カテゴリー2");
		
		categoryRepository.save(category1);
		categoryRepository.save(category2);
		
		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(category1);
		
		Word word2 = new Word();
		word2.setWordName("word2");
		word2.setContent("content2");
		word2.setCategory(category2);
		
		Word word3 = new Word();
		word3.setWordName("word3");
		word3.setContent("content3");
		word3.setCategory(category1);
		
		wordRepository.save(word1);
		wordRepository.save(word2);
		wordRepository.save(word3);
		
		List<Word> resultList = wordRepository.findByCategoryId(category1.getId());
		assertThat(resultList).hasSize(2);
//		assertEquals(2,resultList.size());
		
//		これだと順番は保証されないのでエラーになる可能性がある
//		assertEquals("カテゴリー1",resultList.get(0).getCategory().getName());
//		assertEquals("word3",resultList.get(1).getWordName());
		
		assertThat(resultList).extracting(Word::getWordName).containsExactlyInAnyOrder("word1", "word3");
	}
	
	@Test
	void testDeleteById() {
		Category category1 = new Category();
		category1.setName("カテゴリー1");
		categoryRepository.save(category1);
		
		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(category1);		
		Word savedWord = wordRepository.save(word1);
		
		//登録したデータのidを取得
		Integer id = savedWord.getId();
		
		assertThat(wordRepository.findById(id)).isPresent();
		wordRepository.deleteById(id);
		assertThat(wordRepository.findById(id)).isNotPresent();
	}

}
