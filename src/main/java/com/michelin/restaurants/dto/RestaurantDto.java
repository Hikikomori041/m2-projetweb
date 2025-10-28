package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record RestaurantDto(
        @NotBlank @Length(max = 90) String name,
        @NotBlank @Length(max = 255) String address,
        List<EvaluationEntity> evaluations,
        String imageUrl
) {
    public static RestaurantDto buildFromEntity(RestaurantEntity restaurantEntity) {
        return new RestaurantDto(
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
                restaurantEntity.getEvaluations(),
                restaurantEntity.getImageUrl()
        );
    }
}

/*
Un restaurant est caractérisé par :

- un identifiant unique (un nombre entier positif)
- un nom (longueur max de 90 caractères)
- une adresse (longueur max de 255 caractères)
- une liste d'évaluations
- une image présentant le restaurant
*/
