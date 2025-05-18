package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Wordbook;
import com.example.demo.exception.UnexpectedException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordbookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final WordbookRepository wordbookRepository;

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Category findByCategoryId(Integer categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(()->new UnexpectedException("指定されたカテゴリは存在しません"));
	}

	@Override
	public Optional<Category> findByName(String categoryName) {
		return categoryRepository.findByName(categoryName);
	}
	
	// CategoryNameに入力があったとき、その名前で登録済みかどうかを調べる
	@Override
	public Optional<Category> findByNameAndWordbookId(String categoryName,Integer wordbookId) {
		return categoryRepository.findByNameAndWordbookId(categoryName,wordbookId);
	}

	@Override
	public List<Category> findByWordbookId(Integer wordbookId) {
		return categoryRepository.findByWordbookId(wordbookId);
	}

	//nameで登録してそのidを返す
	@Override
	public Category addCategory(String categoryName, Integer wordbookId) {
		Category category = new Category();
		Wordbook wordbook = wordbookRepository.findById(wordbookId)
				.orElseThrow(()->new UnexpectedException("指定された単語帳が見つかりません"));
		category.setName(categoryName);
		category.setWordbook(wordbook);
		Category savedCategory = categoryRepository.save(category);
		return savedCategory;
	}

	@Override
	public boolean deleteByCategoryId(Integer categoryId) {
		Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
		if(categoryOpt.isPresent()) {
			return true;
		}else {
			return false;
		}
	}

}
