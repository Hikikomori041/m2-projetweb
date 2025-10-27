package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.RestaurantEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RestaurantDto(
        @Min(0) Long id,
        @NotBlank @Length(max = 90) String name,
        @NotBlank @Length(max = 255) String address,
//        List<Evaluation> evaluations,
        String imageUrl
) {
    public static RestaurantDto buildFromEntity(RestaurantEntity restaurantEntity) {
        return new RestaurantDto(
                 restaurantEntity.getId(),
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
//                restaurantEntity.getEvaluations(),
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
