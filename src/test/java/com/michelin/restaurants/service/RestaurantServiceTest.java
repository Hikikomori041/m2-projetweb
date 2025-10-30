package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Spy
    @InjectMocks
    private RestaurantService restaurantServiceSpy;

// =============================================
// TESTS D'OBTENTION DE TOUS LES RESTAURANTS AVEC LEURS MOYENNES

    @Test
    void getAllFullRestaurants_shouldReturnRestaurantsWithAverage() {
        // given
        RestaurantEntity resto1 = new RestaurantEntity();
        resto1.setId(1L);
        resto1.setName("Chez Mario");

        RestaurantEntity resto2 = new RestaurantEntity();
        resto2.setId(2L);
        resto2.setName("Pizza World");

        List<RestaurantEntity> allRestaurants = List.of(resto1, resto2);

        doReturn(allRestaurants).when(restaurantServiceSpy).getAllRestaurants();
        doReturn(3.5f).when(restaurantServiceSpy).getRestaurantAverage(resto1);
        doReturn(-1f).when(restaurantServiceSpy).getRestaurantAverage(resto2);

        // when
        List<FullRestaurantDto> result = restaurantServiceSpy.getAllFullRestaurants();

        // then
        assertEquals(2, result.size());
        assertEquals(3.5f, result.get(0).moyenne());
        assertEquals(-1f, result.get(1).moyenne());

        verify(restaurantServiceSpy).getAllRestaurants();
        verify(restaurantServiceSpy, times(2)).getRestaurantAverage(any());
    }

    @Test
    void getAllFullRestaurants_shouldReturnEmptyListWhenNoRestaurants() {
        // given
        doReturn(List.of()).when(restaurantServiceSpy).getAllRestaurants();

        // when
        List<FullRestaurantDto> result = restaurantServiceSpy.getAllFullRestaurants();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restaurantServiceSpy).getAllRestaurants();
        verify(restaurantServiceSpy, never()).getRestaurantAverage(any());
    }

// =============================================
// TESTS D'OBTENTION D'UN RESTAURANT

    @Test
    void getFullRestaurantById_shouldReturnRestaurantWithAverage() {
        // given
        RestaurantEntity resto = new RestaurantEntity();
        resto.setId(1L);
        resto.setName("Chez Mario");

        doReturn(resto).when(restaurantServiceSpy).getRestaurantById(1L);
        doReturn(4.0f).when(restaurantServiceSpy).getRestaurantAverage(resto);

        // when
        FullRestaurantDto result = restaurantServiceSpy.getFullRestaurantById(1L);

        // then
        assertEquals(4.0f, result.moyenne());
        assertEquals("Chez Mario", result.restaurant().name());
        verify(restaurantServiceSpy).getRestaurantById(1L);
        verify(restaurantServiceSpy).getRestaurantAverage(resto);
    }

    @Test
    void getFullRestaurantById_shouldReturnRestaurantWithNoEvaluations() {
        // given
        RestaurantEntity resto = new RestaurantEntity();
        resto.setId(2L);
        resto.setName("La Cantina");

        doReturn(resto).when(restaurantServiceSpy).getRestaurantById(2L);
        doReturn(-1f).when(restaurantServiceSpy).getRestaurantAverage(resto);

        // when
        FullRestaurantDto result = restaurantServiceSpy.getFullRestaurantById(2L);

        // then
        assertEquals(-1f, result.moyenne());
        assertEquals("La Cantina", result.restaurant().name());
        verify(restaurantServiceSpy).getRestaurantById(2L);
        verify(restaurantServiceSpy).getRestaurantAverage(resto);
    }

    @Test
    void getFullRestaurantById_shouldThrowWhenRestaurantNotFound() {
        // given
        doThrow(new NoSuchElementException("Restaurant introuvable"))
                .when(restaurantServiceSpy).getRestaurantById(99L);

        // then
        assertThrows(NoSuchElementException.class, () ->
                restaurantServiceSpy.getFullRestaurantById(99L)
        );

        verify(restaurantServiceSpy).getRestaurantById(99L);
        verify(restaurantServiceSpy, never()).getRestaurantAverage(any());
    }


// =============================================
// TESTS D'AJOUT D'UN RESTAURANT

    @Test
    void addRestaurant_shouldSaveWhenAdmin() {
        // given
        RestaurantDto dto = new RestaurantDto("Chez Mario", "1 rue du Four", "image.jpg");
        RestaurantEntity entity = RestaurantEntity.buildFromDto(dto);

        when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(entity);

        // when
        RestaurantEntity result = restaurantService.addRestaurant(dto, true);

        // then
        assertNotNull(result);
        assertEquals("Chez Mario", result.getName());
        verify(restaurantRepository).save(any(RestaurantEntity.class));
    }

    @Test
    void addRestaurant_shouldThrowWhenNotAdmin() {
        // given
        RestaurantDto dto = new RestaurantDto("Chez Luigi", "2 rue des Pâtes", "photo.png");

        // then
        assertThrows(AccessDeniedException.class, () ->
                restaurantService.addRestaurant(dto, false)
        );

        verify(restaurantRepository, never()).save(any());
    }

// =============================================
// TESTS DE MODIFICATION D'UN RESTAURANT

    @Test
    void updateRestaurant_shouldUpdateWhenAdminAndExists() {
        // given
        Long id = 1L;
        EditRestaurantDto editDto = new EditRestaurantDto("Chez Mario Deluxe", "42 rue des Pâtes");

        RestaurantEntity existing = new RestaurantEntity();
        existing.setId(id);
        existing.setName("Chez Mario");
        existing.setAddress("1 rue du Four");

        when(restaurantRepository.existsById(id)).thenReturn(true);
        doReturn(existing).when(restaurantServiceSpy).getRestaurantById(id);
        when(restaurantRepository.save(any(RestaurantEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        RestaurantEntity result = restaurantServiceSpy.updateRestaurant(id, editDto, true);

        // then
        assertEquals("Chez Mario Deluxe", result.getName());
        assertEquals("42 rue des Pâtes", result.getAddress());
        verify(restaurantRepository).existsById(id);
        verify(restaurantServiceSpy).getRestaurantById(id);
        verify(restaurantRepository).save(existing);
    }

    @Test
    void updateRestaurant_shouldThrowWhenNotAdmin() {
        // given
        EditRestaurantDto editDto = new EditRestaurantDto("Chez Luigi", "2 rue des Raviolis");

        // then
        assertThrows(AccessDeniedException.class, () ->
                restaurantServiceSpy.updateRestaurant(1L, editDto, false)
        );

        verify(restaurantRepository, never()).save(any());
        verify(restaurantRepository, never()).existsById(any());
    }

    @Test
    void updateRestaurant_shouldThrowWhenRestaurantNotFound() {
        // given
        Long id = 99L;
        EditRestaurantDto editDto = new EditRestaurantDto("Chez Fantôme", "Aucune adresse");

        when(restaurantRepository.existsById(id)).thenReturn(false);

        // then
        assertThrows(NoSuchElementException.class, () ->
                restaurantServiceSpy.updateRestaurant(id, editDto, true)
        );

        verify(restaurantRepository).existsById(id);
        verify(restaurantRepository, never()).save(any());
    }

}
