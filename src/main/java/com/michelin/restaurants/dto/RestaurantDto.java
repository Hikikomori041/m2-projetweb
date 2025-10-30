package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RestaurantDto(
        @NotBlank @Length(max = 90) String name,
        @NotBlank @Length(max = 255) String address,
        String imageUrl
) {
    public static RestaurantDto buildFromEntity(RestaurantEntity restaurantEntity) {
        return new RestaurantDto(
            restaurantEntity.getName(),
            restaurantEntity.getAddress(),
            restaurantEntity.getImageUrl()
        );
    }
}