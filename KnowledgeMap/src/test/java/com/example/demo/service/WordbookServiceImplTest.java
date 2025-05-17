package com.example.demo.service;

import static org.assertj.core.api.Assertions.*;
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

    @Test
    void testSave() {
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
        boolean result = wordbookService.deleteById(10);

        assertThat(result).isTrue();
        verify(wordRepository).deleteByWordbookId(10);
        verify(categoryRepository).deleteByWordbookId(10);
        verify(wordbookRepository).deleteById(10);
    }

    @Test
    void testDeleteById_NotFound() {
        doReturn(Optional.empty()).when(wordbookRepository).findById(999);
        boolean result = wordbookService.deleteById(999);

        assertThat(result).isFalse();
        verify(wordRepository, never()).deleteByWordbookId(anyInt());
        verify(categoryRepository, never()).deleteByWordbookId(anyInt());
        verify(wordbookRepository, never()).deleteById(anyInt());
    }
}
