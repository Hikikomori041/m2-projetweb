package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EditRestaurantDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.service.RestaurantService;
import com.michelin.restaurants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Restaurants", description = "Endpoints pour la gestion des restaurants")
@RestController
@RequestMapping("/restaurant")
@Slf4j
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
    @Operation(summary = "Récupère tous les restaurants")
    public List<FullRestaurantDto> getAllFullRestaurants() {
        return this.restaurantService.getAllFullRestaurants();
    }

    // Récupère un restaurant en particulier, avec sa moyenne d'évaluations
    @GetMapping("/{id}")
    @Operation(summary = "Récupère un restaurant via son identifiant, avec sa moyenne d'évaluations (-1 si aucune)")
    public FullRestaurantDto getFullRestaurantById(@PathVariable("id") Long id) {
        return this.restaurantService.getFullRestaurantById(id);
    }

    // Crée un restaurant
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    @Operation(summary = "Créer un restaurant (Administrateur seulement)")
    public RestaurantDto addRestaurant(@Valid @RequestBody RestaurantDto restaurantDto, @AuthenticationPrincipal Jwt jwt) {
        return RestaurantDto.buildFromEntity(this.restaurantService.addRestaurant(restaurantDto, this.userService.isAdmin(jwt)));
    }

    // Met à jour le nom et l'adresse d'un restaurant
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour le nom et l'adresse d'un restaurant (Administrateur seulement)")
    public RestaurantDto updateRestaurant(@PathVariable("id") Long id, @RequestBody EditRestaurantDto editRestaurantDto, @AuthenticationPrincipal Jwt jwt) {
        return RestaurantDto.buildFromEntity(this.restaurantService.updateRestaurant(id, editRestaurantDto, this.userService.isAdmin(jwt)));
    }
}


/* Notes

    J'ai pensé d'abord mettre les throw AccessDeniedException ici (si l'utilisateur connecté n'est pas un admin),
    mais je me suis dit que pour les tests unitaires, c'est mieux de faire cette vérification dans RestaurantService.
*/