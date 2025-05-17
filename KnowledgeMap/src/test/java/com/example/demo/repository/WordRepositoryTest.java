package com.example.demo.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.entity.Word;
import com.example.demo.entity.Wordbook;

@DataJpaTest
public class WordRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private WordRepository wordRepository;
	@Autowired
	private WordbookRepository wordbookRepository;
	@Autowired
	private UserRepository userRepository;
	
    User testUser;
    Wordbook testWordbook;
    Category testCategory;
    Category testCategory2;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setUsername("testuser");
		testUser.setPassword("pass"); 
	    userRepository.save(testUser);
		
		testWordbook = new Wordbook();
		testWordbook.setName("testWordbook");
		testWordbook.setUser(testUser);
		wordbookRepository.save(testWordbook);	
		
		testCategory = new Category();
		testCategory.setName("testCategory");
		testCategory.setWordbook(testWordbook);
		categoryRepository.save(testCategory);
		
		testCategory2 = new Category();
		testCategory2.setName("testCategory2");
		testCategory2.setWordbook(testWordbook);
		categoryRepository.save(testCategory2);
	}
	@Test
	void testSave() {
	
		Word word = new Word();
		word.setWordName("word1");
		word.setCategory(testCategory);
		word.setContent("content1");
		word.setWordbook(testWordbook);

		Word savedWord = wordRepository.save(word);

		Word getWord = wordRepository.findById(savedWord.getId())
				.orElseThrow();

		assertThat(getWord.getWordName()).isEqualTo("word1");
		assertThat(getWord.getContent()).isEqualTo("content1");
		assertThat(getWord.getCategory().getName()).isEqualTo("testCategory");
		assertThat(getWord.getWordbook().getName()).isEqualTo("testWordbook");
	}

	//wordNameによる検索
	@Test
	void testFindbyWordName() {

		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory);
		word1.setWordbook(testWordbook);

		wordRepository.save(word1);

		Optional<Word> wordOpt = wordRepository.findByWordName(word1.getWordName());
		assertThat(wordOpt).isPresent();
		assertThat(wordOpt.get().getContent()).isEqualTo("content1");

	}

	//categoryIdによる検索
	@Test
	void testFindByCategoryId() {

		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory);
		word1.setWordbook(testWordbook);

		Word word2 = new Word();
		word2.setWordName("word2");
		word2.setContent("content2");
		word2.setCategory(testCategory2);
		word2.setWordbook(testWordbook);

		Word word3 = new Word();
		word3.setWordName("word3");
		word3.setContent("content3");
		word3.setCategory(testCategory);
		word3.setWordbook(testWordbook);

		wordRepository.save(word1);
		wordRepository.save(word2);
		wordRepository.save(word3);

		List<Word> resultList = wordRepository.findByCategoryId(testCategory.getId());
		assertThat(resultList).hasSize(2);
		//		assertEquals(2,resultList.size());

		//		これだと順番は保証されないのでエラーになる可能性がある
		//		assertEquals("カテゴリー1",resultList.get(0).getCategory().getName());
		//		assertEquals("word3",resultList.get(1).getWordName());

		assertThat(resultList).extracting(Word::getWordName).containsExactlyInAnyOrder("word1", "word3");
	}

	@Test
	void testDeleteById() {

		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory);
		word1.setWordbook(testWordbook);
		
		Word savedWord = wordRepository.save(word1);

		//登録したデータのidを取得
		Integer id = savedWord.getId();
		assertThat(wordRepository.findById(id)).isPresent();
		
		wordRepository.deleteById(id);
		assertThat(wordRepository.findById(id)).isNotPresent();
	}
	@Test
	void findByWordNameAndWordbookId() {
		
		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory);
		word1.setWordbook(testWordbook);
		
		wordRepository.save(word1);
		
		Optional<Word> wordOpt =  wordRepository.findByWordNameAndWordbookId("word1",testWordbook.getId());
		assertThat(wordOpt).isPresent();
		assertThat(wordOpt.get().getWordName()).isEqualTo("word1");
	}
	@Test
	void deleteByWordbookId() {
		Word word1 = new Word();
		word1.setWordName("word1");
		word1.setContent("content1");
		word1.setCategory(testCategory);
		word1.setWordbook(testWordbook);
		Word savedWord = wordRepository.save(word1);
		
		wordRepository.deleteByWordbookId(testWordbook.getId());
		
		assertThat(wordRepository.findById(savedWord.getId())).isNotPresent();
		
	}

}
