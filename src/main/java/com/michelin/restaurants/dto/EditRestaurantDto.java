package com.michelin.restaurants.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EditRestaurantDto(
        @NotBlank @Length(max = 90) String name,
        @NotBlank @Length(max = 255) String address
) {}

/* Note

Ce record sert de modèle pour la modification de restaurant (qui ne demande que le nom et l'adresse).
 */