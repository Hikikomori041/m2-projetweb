package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // Récupère tous les restaurants
    @GetMapping()
    public List<RestaurantDto> getAllRestaurants() {
        //todo: ajouter les évaluations
        return this.restaurantService.getAllRestaurants()
                .stream()
                .map(RestaurantDto::buildFromEntity)
                .toList();
    }

    // Récupère un restaurant en particulier
    @GetMapping("/{id}")
    //todo: ajouter les évaluations
    public RestaurantDto getRestaurantById(@PathVariable("id") Long id) {
        return RestaurantDto.buildFromEntity(this.restaurantService.getRestaurantById(id));
    }

    // Crée un restaurant
    @PostMapping()
    public RestaurantDto addRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
        return RestaurantDto.buildFromEntity(this.restaurantService.addRestaurant(restaurantDto));
    }

    // Met à jour le nom et l'adresse d'un restaurant
    @PutMapping("/{id}")
    public RestaurantDto updateRestaurant(@PathVariable("id") Long id, @RequestBody EditRestaurantDto editRestaurantDto) {
        return RestaurantDto.buildFromEntity(this.restaurantService.updateRestaurant(id, editRestaurantDto));
    }
}


/*
- La possibilité de récupérer tous les restaurants*
- La possibilité de récupérer un restaurant en particulier*
- La possibilité de créer un restaurant*
- La possibilité de mettre à jour le nom et l'adresse d'un restaurant

Les routes retournant un (ou plusieurs) restaurant (marquées par *) doivent aussi retourner la moyenne des notes du-dit restaurant dans une propriété nommée "moyenne". Si le restaurant ne dispose d'aucune evaluation, la moyenne est de -1.

Les cas d'erreur doivent être gérés pour retourner une erreur (404, 500, etc) contenant
- Un code
- Un message expliquant l'erreur.

*/