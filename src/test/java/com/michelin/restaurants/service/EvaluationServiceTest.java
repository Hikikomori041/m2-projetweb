package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationIndexService evaluationIndexService;

    @InjectMocks
    private EvaluationService evaluationService;

// =============================================
// TESTS D'AJOUT D'UNE ÉVALUATION

    @Test
    void addEvaluation_shouldSaveEvaluationWithAuthor() {
        // --- given ---
        final String author = "Nico";
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);

        EvaluationDto dto = new EvaluationDto(1L, "Très bon !", 3);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        EvaluationEntity savedEntity = new EvaluationEntity();
        savedEntity.setId(99L);
        savedEntity.setAuthor(author);
        when(evaluationRepository.save(any(EvaluationEntity.class))).thenReturn(savedEntity);

        // --- when ---
        EvaluationEntity result = evaluationService.addEvaluation(dto, author);

        // --- then ---
        assertEquals(99L, result.getId());
        assertEquals(author, result.getAuthor());
        verify(restaurantRepository).findById(1L);
        verify(evaluationRepository).save(any(EvaluationEntity.class));
    }

    @Test
    void addEvaluation_shouldThrowIfRestaurantNotFound() {
        // --- given ---
        final String author = "Nico";
        EvaluationDto dto = new EvaluationDto(1L, "Très bon !", 3);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // --- expect ---
        assertThrows(NoSuchElementException.class, () ->
                evaluationService.addEvaluation(dto, author)
        );

        verify(evaluationRepository, never()).save(any());
    }

// =============================================
// TESTS DE SUPPRESSION D'UNE ÉVALUATION

    @Test
    void deleteEvaluation_shouldDeleteWhenAuthorMatches() {
        // given
        final String author = "Nico";
        EvaluationEntity entity = new EvaluationEntity();
        entity.setId(1L);
        entity.setAuthor(author);

        when(evaluationRepository.existsById(1L)).thenReturn(true);
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(entity));

        // when
        EvaluationEntity result = evaluationService.deleteEvaluation(1L, author, false);

        // then
        verify(evaluationRepository).deleteById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void deleteEvaluation_shouldDeleteWhenUserIsAdmin() {
        // given
        EvaluationEntity entity = new EvaluationEntity();
        entity.setId(2L);
        entity.setAuthor("Alex");

        when(evaluationRepository.existsById(2L)).thenReturn(true);
        when(evaluationRepository.findById(2L)).thenReturn(Optional.of(entity));

        // when
        EvaluationEntity result = evaluationService.deleteEvaluation(2L, "Nico", true);

        // then
        verify(evaluationRepository).deleteById(2L);
        assertEquals(2L, result.getId());
    }

    @Test
    void deleteEvaluation_shouldThrowIfNotAuthorAndNotAdmin() {
        // given
        EvaluationEntity entity = new EvaluationEntity();
        entity.setId(3L);
        entity.setAuthor("Inconnu");

        when(evaluationRepository.existsById(3L)).thenReturn(true);
        when(evaluationRepository.findById(3L)).thenReturn(Optional.of(entity));

        // then
        assertThrows(AccessDeniedException.class, () ->
                evaluationService.deleteEvaluation(3L, "Nico", false)
        );

        verify(evaluationRepository, never()).deleteById(any());
    }

    @Test
    void deleteEvaluation_shouldThrowIfEvaluationNotFound() {
        // given
        when(evaluationRepository.existsById(10L)).thenReturn(false);

        // then
        assertThrows(NoSuchElementException.class, () ->
                evaluationService.deleteEvaluation(10L, "Nico", false)
        );

        verify(evaluationRepository, never()).deleteById(any());
    }

// =============================================
// TESTS DE RECHERCHE D'ÉVALUATIONS PAR AUTEUR

    @Test
    void getEvaluationsByAuthor_shouldReturnListFromRepository() {
        // given
        String author = "Nico";

        RestaurantEntity resto = new RestaurantEntity();
        resto.setId(1L);
        resto.setName("Au boudin moment");

        EvaluationDto dto1 = new EvaluationDto(1L, "Super resto", 3);
        EvaluationDto dto2 = new EvaluationDto(1L, "À éviter", 0);

        EvaluationEntity e1 = EvaluationEntity.buildFromDto(dto1, resto);
        EvaluationEntity e2 = EvaluationEntity.buildFromDto(dto2, resto);

        List<EvaluationEntity> expectedList = List.of(e1, e2);

        when(evaluationRepository.findAllByAuthor(author)).thenReturn(expectedList);

        // when
        List<EvaluationEntity> result = evaluationService.getEvaluationsByAuthor(author);

        // then
        assertEquals(expectedList, result);
        verify(evaluationRepository).findAllByAuthor(author);
    }

    @Test
    void getEvaluationsByAuthor_shouldReturnEmptyListWhenNoEvaluationsFound() {
        // given
        String author = "Inconnu";
        when(evaluationRepository.findAllByAuthor(author)).thenReturn(List.of());

        // when
        List<EvaluationEntity> result = evaluationService.getEvaluationsByAuthor(author);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(evaluationRepository).findAllByAuthor(author);
    }


// =============================================
// TESTS DE RECHERCHE D'ÉVALUATIONS PAR MOTS-CLÉS

    @Test
    void getEvaluationsByKeywords_shouldReturnMatchingEvaluations() {
        // given
        List<String> keywords = List.of("service", "prix");
        List<String> matchingIds = List.of("1", "2");

        RestaurantEntity resto = new RestaurantEntity();
        resto.setId(1L);
        resto.setName("Chez Mario");

        EvaluationEntity e1 = new EvaluationEntity();
        e1.setId(1L);
        e1.setComment("Super service, bon prix !");
        e1.setRestaurant(resto);

        EvaluationEntity e2 = new EvaluationEntity();
        e2.setId(2L);
        e2.setComment("Service un peu lent mais prix correct.");
        e2.setRestaurant(resto);

        when(evaluationIndexService.searchEvaluation("service prix")).thenReturn(matchingIds);
        when(evaluationRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(e1, e2));

        // when
        List<EvaluationDto> result = evaluationService.getEvaluationsByKeywords(keywords);

        // then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto ->
                dto.comment().toLowerCase().contains("service") ||
                        dto.comment().toLowerCase().contains("prix"))
        );

        verify(evaluationIndexService).searchEvaluation("service prix");
        verify(evaluationRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    void getEvaluationsByKeywords_shouldReturnEmptyListWhenNoMatch() {
        // given
        List<String> keywords = List.of("foie gras");

        when(evaluationIndexService.searchEvaluation("foie gras")).thenReturn(List.of());
        when(evaluationRepository.findAllById(List.of())).thenReturn(List.of());

        // when
        List<EvaluationDto> result = evaluationService.getEvaluationsByKeywords(keywords);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(evaluationIndexService).searchEvaluation("foie gras");
        verify(evaluationRepository).findAllById(List.of());
    }
}

