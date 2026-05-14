package com.gamerstore.suggestion_service.services;

import com.gamerstore.shared.suggestion.dto.SuggestionProductDTO;
import com.gamerstore.suggestion_service.clients.ProductClient;
import com.gamerstore.suggestion_service.dto.ViewRequestDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private SuggestionService suggestionService;

    @BeforeEach
    void setUp() {

        when(redisTemplate.opsForSet())
                .thenReturn(setOperations);
    }

    @Test
    @DisplayName("registerView | Deve registrar visualização corretamente")
    void registerView_shouldRegisterViewCorrectly() {

        ViewRequestDTO dto = new ViewRequestDTO(
                1L,
                10L);

        suggestionService.registerView(dto);

        verify(setOperations, times(1))
                .add(
                        "user:1:views",
                        10L);

        verify(setOperations, times(1))
                .add(
                        "product:10:users",
                        1L);

        verify(redisTemplate, times(1))
                .expire(
                        "user:1:views",
                        30,
                        TimeUnit.DAYS);

        verify(redisTemplate, times(1))
                .expire(
                        "product:10:users",
                        30,
                        TimeUnit.DAYS);
    }

    @Test
    @DisplayName("suggestProducts | Deve retornar lista vazia quando não houver usuários")
    void suggestProducts_shouldReturnEmptyListWhenNoUsers() {

        when(setOperations.members(
                "product:1:users")).thenReturn(null);

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(productClient, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName("suggestProducts | Deve retornar lista vazia quando conjunto de usuários estiver vazio")
    void suggestProducts_shouldReturnEmptyListWhenUsersSetIsEmpty() {

        when(setOperations.members(
                "product:1:users")).thenReturn(Set.of());

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(productClient, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName("suggestProducts | Deve ignorar usuários sem produtos visualizados")
    void suggestProducts_shouldIgnoreUsersWithoutViewedProducts() {

        when(setOperations.members(
                "product:1:users")).thenReturn(Set.of(1L));

        when(setOperations.members(
                "user:1:views")).thenReturn(null);

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(productClient, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName("suggestProducts | Deve retornar sugestões ordenadas por frequência")
    void suggestProducts_shouldReturnSuggestionsOrderedByFrequency() {

        SuggestionProductDTO dto2 = mock(SuggestionProductDTO.class);

        SuggestionProductDTO dto3 = mock(SuggestionProductDTO.class);

        when(setOperations.members(
                "product:1:users")).thenReturn(
                        Set.of(1L, 2L));

        when(setOperations.members(
                "user:1:views")).thenReturn(
                        Set.of(1L, 2L, 3L));

        when(setOperations.members(
                "user:2:views")).thenReturn(
                        Set.of(1L, 2L));

        when(productClient.findById(2L))
                .thenReturn(dto2);

        when(productClient.findById(3L))
                .thenReturn(dto3);

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertEquals(2, result.size());

        assertEquals(dto2, result.get(0));

        assertEquals(dto3, result.get(1));

        verify(productClient, times(1))
                .findById(2L);

        verify(productClient, times(1))
                .findById(3L);
    }

    @Test
    @DisplayName("suggestProducts | Não deve sugerir o próprio produto")
    void suggestProducts_shouldNotSuggestSameProduct() {

        when(setOperations.members(
                "product:1:users")).thenReturn(
                        Set.of(1L));

        when(setOperations.members(
                "user:1:views")).thenReturn(
                        Set.of(1L));

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(productClient, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName("suggestProducts | Deve limitar sugestões em 5 produtos")
    void suggestProducts_shouldLimitSuggestionsToFiveProducts() {

        when(setOperations.members(
                "product:1:users")).thenReturn(
                        Set.of(1L));

        when(setOperations.members(
                "user:1:views")).thenReturn(
                        Set.of(
                                2L,
                                3L,
                                4L,
                                5L,
                                6L,
                                7L));

        for (long i = 2L; i <= 6L; i++) {

            when(productClient.findById(i))
                    .thenReturn(
                            mock(
                                    SuggestionProductDTO.class));
        }

        List<SuggestionProductDTO> result = suggestionService
                .suggestProducts(1L);

        assertNotNull(result);

        assertEquals(5, result.size());

        verify(productClient, times(5))
                .findById(anyLong());
    }
}