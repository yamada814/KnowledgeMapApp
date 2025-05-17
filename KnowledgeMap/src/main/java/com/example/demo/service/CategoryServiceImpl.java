package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Wordbook;
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
	public Optional<Category> findByCategoryId(Integer categoryId) {
		return categoryRepository.findById(categoryId);
	}

	@Override
	public Optional<Category> findByName(String categoryName) {
		return categoryRepository.findByName(categoryName);
	}
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
		Optional<Wordbook> wordbookOpt = wordbookRepository.findById(wordbookId);
		if (wordbookOpt.isEmpty()) {
			throw new IllegalArgumentException("指定されたwordbookIdが見つかりません" + wordbookId);
		}
		category.setName(categoryName);
		category.setWordbook(wordbookOpt.get());
		Category savedCategory = categoryRepository.save(category);
		return savedCategory;
	}



	@Override
	public boolean deleteByCategoryId(Integer categoryId) {
		Optional<Category> categoryOpt = findByCategoryId(categoryId);
		if(categoryOpt.isPresent()) {
			categoryRepository.deleteById(categoryId);
			return true;
		}else {
			return false;
		}
	}

}
