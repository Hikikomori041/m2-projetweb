package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurantById(Long id) {
        return this.restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le restaurant avec l'identifiant '" + id + "' n'a pas été trouvé"));
    }

    public RestaurantEntity addRestaurant(RestaurantDto restaurantDto) {
        return this.restaurantRepository.save(RestaurantEntity.buildFromDto(restaurantDto));
    }

    public RestaurantEntity updateRestaurant(Long id, EditRestaurantDto editRestaurantDto) {
        if(!this.restaurantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le restaurant avec l'identifiant '" + id + "' n'existe pas");
        }
        RestaurantEntity restaurantEntity = this.getRestaurantById(id);
        restaurantEntity.setName(editRestaurantDto.name());
        restaurantEntity.setAddress(editRestaurantDto.address());

        return this.restaurantRepository.save(restaurantEntity);
    }
}
