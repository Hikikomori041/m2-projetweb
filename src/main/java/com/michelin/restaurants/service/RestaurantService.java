package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


    // Retourne la liste complète des restaurants, avec la moyenne des notes de leurs évaluations (-1 si aucune)
    public List<FullRestaurantDto> getAllFullRestaurants() {
        List<RestaurantEntity> allRestaurants = this.getAllRestaurants();

        List<FullRestaurantDto> fullRestaurants = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            fullRestaurants.add(new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity)));
        }

        return fullRestaurants;
    }

    // Retourne un restaurant via son identifiant, avec la moyenne des notes de ses évaluations (-1 si aucune)
    public FullRestaurantDto getFullRestaurantById(Long id) {
        RestaurantEntity restaurantEntity = getRestaurantById(id);

        return new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity));
    }

    // Ajoute un restaurant (Administrateur seulement)
    public RestaurantEntity addRestaurant(RestaurantDto restaurantDto, boolean isAdmin) {
        if (!isAdmin) throw new AccessDeniedException("Vous devez être administrateur pour faire ajouter un restaurant !");

        return this.restaurantRepository.save(RestaurantEntity.buildFromDto(restaurantDto));
    }

    // Met à jour le nom et l'adresse d'un restaurant (Administrateur seulement)
    public RestaurantEntity updateRestaurant(Long id, EditRestaurantDto editRestaurantDto, boolean isAdmin) {
        if (!isAdmin) throw new AccessDeniedException("Vous devez être administrateur pour modifier un restaurant !");
        if(!this.restaurantRepository.existsById(id)) throw new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas");

        RestaurantEntity restaurantEntity = this.getRestaurantById(id);
        restaurantEntity.setName(editRestaurantDto.name());
        restaurantEntity.setAddress(editRestaurantDto.address());

        return this.restaurantRepository.save(restaurantEntity);
    }


//======================
// Sous-fonctions

    public List<RestaurantEntity> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurantById(Long id) {
        return this.restaurantRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas"));
    }

    public float getRestaurantAverage(RestaurantEntity restaurantEntity) {
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
