package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.service.RestaurantService;
import com.michelin.restaurants.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final UserService userService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    // Récupère tous les restaurants
    @GetMapping()
    public List<FullRestaurantDto> getAllFullRestaurants() {
        return this.restaurantService.getAllFullRestaurants();
    }

    // Récupère un restaurant en particulier, avec sa moyenne d'évaluations
    @GetMapping("/{id}")
    public FullRestaurantDto getFullRestaurantById(@PathVariable("id") Long id) {
        return this.restaurantService.getFullRestaurantById(id);
    }

    // Crée un restaurant
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public RestaurantDto addRestaurant(@Valid @RequestBody RestaurantDto restaurantDto, @AuthenticationPrincipal Jwt jwt) {
        if (!this.userService.isAdmin(jwt)) {
            throw new AccessDeniedException("Vous devez être administrateur pour faire ajouter un restaurant !");
        }

        return RestaurantDto.buildFromEntity(this.restaurantService.addRestaurant(restaurantDto));
    }

    // Met à jour le nom et l'adresse d'un restaurant
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public RestaurantDto updateRestaurant(@PathVariable("id") Long id, @RequestBody EditRestaurantDto editRestaurantDto, @AuthenticationPrincipal Jwt jwt) {
        if (!this.userService.isAdmin(jwt)) {
            throw new AccessDeniedException("Vous devez être administrateur pour modifier un restaurant !");
        }
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