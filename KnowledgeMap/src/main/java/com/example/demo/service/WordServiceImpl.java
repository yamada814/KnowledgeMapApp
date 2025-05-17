package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.WordDetailDto;
import com.example.demo.dto.WordDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Word;
import com.example.demo.entity.Wordbook;
import com.example.demo.form.WordForm;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRelationRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.repository.WordbookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
	private final WordRepository wordRepository;
	private final CategoryRepository categoryRepository;
	private final WordRelationRepository wordRelationRepository;
	private final WordbookRepository wordbookRepository;

	// WordForm型からWord型への変換を行うユーティリティメソッド
	public void transferWordFormToWord(Word word, WordForm wordForm) {

		word.setWordName(wordForm.getWordName());
		word.setContent(wordForm.getContent());

		// category (Integer -> Category へ変換)
		Optional<Category> categoryOpt = categoryRepository.findById(wordForm.getCategoryId());
		if (categoryOpt.isPresent()) {
			word.setCategory(categoryOpt.get());
		}

		// wordbook (Integer -> Wordbook へ変換)
		Optional<Wordbook> wordbookOpt = wordbookRepository.findById(wordForm.getWordbookId());
		if (wordbookOpt.isPresent()) {
			word.setWordbook(wordbookOpt.get());
		}

		// relatedWords (List<Integer> -> List<Word> へ変換)		
		List<Word> relatedWords = new ArrayList<>();
		
		if (wordForm.getRelatedWordIds() != null 
				// && word.getId() != null
				 && !wordForm.getRelatedWordIds().contains(word.getId())
				) {// List<>.contains(null)はfalseを返す
			relatedWords = wordForm.getRelatedWordIds().stream()
					.map(wordId -> wordRepository.findById(wordId))
					.filter(Optional::isPresent)
					.map(Optional::get)
					//@ManyToManyのついてるフィールドは、Hibernateが内部で clear()やadd()を行う可能性があるので mutable である必要がある
					// .toList()はimmutableとなり、clear()が呼ばれたときにjava.lang.UnsupportedOperationExceptionが発生する
					.collect(Collectors.toCollection(ArrayList::new));
		}
		word.setRelatedWords(relatedWords);
	}

	// 関連語の単語名を取得する (List<Integer> -> List<String>)
	@Override
	public List<String> getRelatedWordNames(WordForm wordForm) {
		return wordForm.getRelatedWordIds().stream()
				.map(this::findById)
				.filter(Optional::isPresent)
				.map(wordOpt -> wordOpt.get().getWordName())
				.toList();
	}

	@Override
	public List<Word> findAll() {
		return wordRepository.findAll();
	}

	@Override
	public Optional<Word> findById(Integer id) {
		return wordRepository.findById(id);
	}

	// wordDetail表示
	//(JSONで返す際に循環参照防止のため WordエンティティではなくWordDto型で返す)
	@Override
	public WordDetailDto findWordDetailDtoById(Integer id) {
		Optional<Word> wordOpt = wordRepository.findById(id);
		WordDetailDto dto = new WordDetailDto();
		if (wordOpt.isPresent()) {
			Word word = wordOpt.get();

			CategoryDto categoryDto = new CategoryDto();
			categoryDto.setId(word.getCategory().getId());
			categoryDto.setName(word.getCategory().getName());

			dto.setId(word.getId());
			dto.setWordName(word.getWordName());
			dto.setContent(word.getContent());
			dto.setCategory(categoryDto);

			List<WordDto> relatedWords = word.getRelatedWords().stream()
					.map(relatedWord -> {
						WordDto WordDto = new WordDto();
						WordDto.setId(relatedWord.getId());
						WordDto.setWordName(relatedWord.getWordName());
						WordDto.setCategoryId(relatedWord.getCategory().getId());
						return WordDto;
					})
					.toList();
			dto.setRelatedWords(relatedWords);
		}
		return dto;
	}

	@Override
	public Optional<Word> findByWordName(String name) {
		return wordRepository.findByWordName(name);
	}

	@Override
	public Optional<Word> findByWordNameAndWordbookId(String name, Integer wordbookId) {
		return wordRepository.findByWordNameAndWordbookId(name, wordbookId);
	}

	@Override
	public List<WordDto> findWordsByCategoryId(Integer categoryId) {
		return wordRepository.findByCategoryId(categoryId).stream()
				.map(word -> {
					WordDto dto = new WordDto();
					dto.setId(word.getId());
					dto.setWordName(word.getWordName());
					dto.setContent(word.getContent());
					dto.setCategoryId(word.getCategory().getId());
					return dto;
				})
				.toList();
	}

	@Override
	public List<Word> findByWordbookId(Integer wordbookId) {
		return wordRepository.findByWordbookId(wordbookId);
	}

	@Override
	@Transactional
	//関連語テーブルから削除してからwordテーブルから削除する
	public boolean deleteById(Integer id) {
		if (wordRepository.findById(id).isEmpty()) {		
			return false;
		}
		wordRelationRepository.deleteByWordId(id);
		wordRelationRepository.deleteByRelatedWordId(id);
		wordRepository.deleteById(id);
		return true;
	}

	@Override
	public Word addWord(WordForm wordForm) {
		Word word = new Word();
		transferWordFormToWord(word, wordForm);
		Word savedWord = wordRepository.save(word);
		//関連語の相互参照
		if (savedWord.getRelatedWords() != null) {
			interactRelatedWord(savedWord);
		}
		return savedWord;
	}

	@Override
	public Word updateWord(Integer id, WordForm wordForm) {
		Optional<Word> wordOpt = wordRepository.findById(id);
		Word word = wordOpt.get();
		transferWordFormToWord(word, wordForm);//WordForm型 -> Word型　の変換
		Word updatedWord = wordRepository.save(word);
		//関連語の相互参照
		if (updatedWord.getRelatedWords() != null) {
			interactRelatedWord(updatedWord);
		}
		return updatedWord;
	}

	// 関連語にも新規作成した単語を関連づけるメソッド
	public void interactRelatedWord(Word savedWord) {
		// 自身のwordを関連語として登録しようとするとConcurrentModificationExceptionが発生するので
		// ループの中で 自身のwordじゃないかをチェック & 自身のrelatedWordsのコピーを作成してループ
		// (関連語フィールドに自身のwordを登録しないようバリデーションチェックはかけているが、念の為)
		List<Word> relatedWordsCopy = new ArrayList<>(savedWord.getRelatedWords());
		for (Word relatedWord : relatedWordsCopy) {
			if (!relatedWord.equals(savedWord)) {
				relatedWord.getRelatedWords().add(savedWord);
			}
			wordRepository.save(relatedWord);
		}
	}
}
