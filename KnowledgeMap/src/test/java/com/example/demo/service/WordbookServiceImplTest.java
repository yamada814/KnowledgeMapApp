package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.WordbookDto;
import com.example.demo.entity.User;
import com.example.demo.entity.Wordbook;
import com.example.demo.exception.DeleteFailException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.WordRelationRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.repository.WordbookRepository;

@ExtendWith(MockitoExtension.class)
public class WordbookServiceImplTest {
	
	@InjectMocks
	WordbookServiceImpl wordbookService;
	
	@Mock
	WordbookRepository wordbookRepository;
	@Mock
	CategoryRepository categoryRepository;
	@Mock
	WordRepository wordRepository;
	@Mock
	WordRelationRepository wordRelationRepository;
	
    User testUser;
    Wordbook testWordbook;
	
	@BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        
        testWordbook = new Wordbook();
        testWordbook.setId(10);
        testWordbook.setName("testWordbook");
        testWordbook.setUser(testUser);
    }

    @Test
    void testFindWordBook() {
        doReturn(List.of(testWordbook)).when(wordbookRepository).findByUserId(1);
        List<Wordbook> result = wordbookService.findWordBook(testUser);

        assertThat(result).containsExactly(testWordbook);
    }
    // 登録 (成功)
    @Test
    void testSave_success() {
        doReturn(testWordbook).when(wordbookRepository).save(any(Wordbook.class));
        WordbookDto result = wordbookService.save(testWordbook);

        assertThat(result.getId()).isEqualTo(testWordbook.getId());
        assertThat(result.getWordbookName()).isEqualTo(testWordbook.getName());
    }


    @Test
    void testFindByWordbookNameAndUserId() {
        doReturn(Optional.of(testWordbook)).when(wordbookRepository).findByNameAndUserId("testWordbook", 1);
        Optional<Wordbook> result = wordbookService.findByWordbookNameAndUserId("testWordbook", 1);

        assertThat(result).isPresent().contains(testWordbook);
    }

    @Test
    void testDeleteById_Success() {
        doReturn(Optional.of(testWordbook)).when(wordbookRepository).findById(10);
        wordbookService.deleteById(10);

        verify(wordRepository).deleteByWordbookId(10);
        verify(categoryRepository).deleteByWordbookId(10);
        verify(wordbookRepository).deleteById(10);
    }
    // 削除実行 (指定idが見つからない)
    @Test
    void testDeleteById_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("指定された単語帳は見つかりません")).when(wordbookRepository).findById(999);
        
        assertThrows(ResourceNotFoundException.class,()->{       	
        	wordbookService.deleteById(999);
        	});
        verify(wordRepository, never()).deleteByWordbookId(anyInt());
        verify(categoryRepository, never()).deleteByWordbookId(anyInt());
        verify(wordbookRepository, never()).deleteById(anyInt());
    }
    // 削除実行 (DBエラー)
    @Test
    void testDeleteById_DeleteFailExceptionn() {
    	doReturn(Optional.of(testWordbook)).when(wordbookRepository).findById(1);
    	doThrow(new DeleteFailException("削除処理中にエラーが発生しました")).when(categoryRepository).deleteByWordbookId(1);
    	
    	assertThrows(DeleteFailException.class,()->{       	
    		wordbookService.deleteById(1);
    	});
    	verify(wordRepository, atLeastOnce()).deleteByWordbookId(anyInt());
    	verify(categoryRepository, atLeastOnce()).deleteByWordbookId(anyInt());
    	verify(wordbookRepository, never()).deleteById(anyInt());

    }
}
