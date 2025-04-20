package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImpleTest {
	
	@InjectMocks
	CategoryServiceImpl categoryServiceImpl;
	
	@Mock
	CategoryRepository categoryRepository;
	
	@Test
	void testFindAll() {
		Category category1 = new Category();
		category1.setName("category1");
		Category category2 = new Category();
		category2.setName("category2");
		List<Category> list = new ArrayList<Category>(List.of(category1,category2));
		
		doReturn(list).when(categoryRepository).findAll();
		
		List<Category> resultList = categoryServiceImpl.findAll();
		assertThat(resultList).containsExactlyInAnyOrder(category1,category2);
	}
	@Test
	void testFindByCategoryId() {
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		
		doReturn(Optional.of(category1)).when(categoryRepository).findById(1);
		
		Optional<Category> categoryOpt = categoryServiceImpl.findByCategoryId(1);
		Category category = categoryOpt.get();
		assertThat(category.getName()).isEqualTo("category1");
	}
	@Test
	void testSearchByName() {
		Category category1 = new Category();
		category1.setId(1);
		category1.setName("category1");
		
		doReturn(Optional.of(category1)).when(categoryRepository).findByName("category1");
		
		Optional<Category> categoryOpt = categoryServiceImpl.findByName("category1");
		Category category = categoryOpt.get();
		assertThat(category.getId()).isEqualTo(1);	
	}
	@Test
	void testAddCategory() {
		
//		Integer id = 1;
//		String categoryName = "category1";
//		Category category1 = new Category();
//		category1.setId(id);
//		category1.setName(categoryName);
//		doReturn(category1).when(categoryRepository).save(any(Category.class));
		
//		実際のDBにsaveメソッドでアクセスする際はinsertした後、
//		引数で渡したエンティティにid番号を設定するのを自動で行ってくれるが、
//		Mockでのテストでは自分でidを設定する必要がある

//	    doReturnではなくdoAnswerを使うことで、
//	    save()に渡された引数を取得して値を設定できる
		String categoryName = "category1";
	    doAnswer(invocation -> {
	        Category arg = invocation.getArgument(0);
	        arg.setId(1); 
	        return arg;
	    }).when(categoryRepository).save(any(Category.class));

	    Category category = categoryServiceImpl.addCategory(categoryName);
	    assertThat(category.getId()).isEqualTo(1);
	}

}
