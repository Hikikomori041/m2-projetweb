package com.michelin.restaurants.dto;

import jakarta.validation.constraints.NotNull;

public record FullRestaurantDto(
        @NotNull Float moyenne,
        @NotNull RestaurantDto restaurant
) {}

/* Notes

Le but de ce record Dto est de permettre de renvoyer un restaurant avec sa moyenne calculée dans un service.

 */