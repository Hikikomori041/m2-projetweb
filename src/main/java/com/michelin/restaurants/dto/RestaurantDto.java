package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.RestaurantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(
    description = "Objet contenant les informations du restaurant",
    example = """
    {
      "name": "Chez Mario",
      "address": "42 rue des Raviolis"
    }
    """
)public record RestaurantDto(
    @NotBlank @Length(max = 90) String name,
    @NotBlank @Length(max = 255) String address
) {
    public static RestaurantDto buildFromEntity(RestaurantEntity restaurantEntity) {
        return new RestaurantDto(
            restaurantEntity.getName(),
            restaurantEntity.getAddress()
        );
    }
}