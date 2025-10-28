package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


    public List<FullRestaurantDto> getAllFullRestaurants() {
        List<RestaurantEntity> allRestaurants = this.getAllRestaurants();

        List<FullRestaurantDto> fullRestaurants = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            fullRestaurants.add(new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity)));
        }

        return fullRestaurants;
    }

    public FullRestaurantDto getFullRestaurantById(Long id) {
        RestaurantEntity restaurantEntity = getRestaurantById(id);

        return new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity));
    }

    public RestaurantEntity addRestaurant(RestaurantDto restaurantDto) {
        return this.restaurantRepository.save(RestaurantEntity.buildFromDto(restaurantDto));
    }

    public RestaurantEntity updateRestaurant(Long id, EditRestaurantDto editRestaurantDto) {
        if(!this.restaurantRepository.existsById(id)) {
            throw new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas");
        }
        RestaurantEntity restaurantEntity = this.getRestaurantById(id);
        restaurantEntity.setName(editRestaurantDto.name());
        restaurantEntity.setAddress(editRestaurantDto.address());

        return this.restaurantRepository.save(restaurantEntity);
    }






    private List<RestaurantEntity> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    private RestaurantEntity getRestaurantById(Long id) {
        return this.restaurantRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas"));
    }

    private float getRestaurantAverage(RestaurantEntity restaurantEntity) {
        float moyenne = -1;
        List<EvaluationEntity> evaluation = restaurantEntity.getEvaluations();
        if (!evaluation.isEmpty()) {
            int somme = 0;
            int nbEvaluations = evaluation.size();
            for (EvaluationEntity evaluationEntity : evaluation) {
                somme += evaluationEntity.getNote();
            }
            moyenne = 1.0f * somme / nbEvaluations; // 1.0f permet de convertir le résultat en float
            moyenne = Math.round(moyenne * 100f) / 100f; // pour ne garder que 2 chiffres après la virgule
        }
        return moyenne;
    }
}
