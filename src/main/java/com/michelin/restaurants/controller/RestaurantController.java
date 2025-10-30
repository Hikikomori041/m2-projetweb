package com.michelin.restaurants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.service.RestaurantService;
import com.michelin.restaurants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Ajoute une image à un restaurant
    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajoute une image à un restaurant (Administrateur seulement)")
    public String addRestaurantImage(@PathVariable("id") Long id, @RequestPart("file") MultipartFile image, @AuthenticationPrincipal Jwt jwt) throws Exception {
        return this.restaurantService.addRestaurantImage(id, image, this.userService.isAdmin(jwt));
    }

    // Récupère l'image d'un restaurant
    @GetMapping(path = "/{id}/image")
    @Operation(summary = "Récupère l'image d'un restaurant")
    public String getRestaurantImage(@PathVariable("id") Long id) throws Exception {
        return this.restaurantService.getRestaurantImage(id);
    }

    // Met à jour le nom et l'adresse d'un restaurant
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour le nom et l'adresse d'un restaurant (Administrateur seulement)")
    public RestaurantDto updateRestaurant(@PathVariable("id") Long id, @RequestBody RestaurantDto editRestaurantDto, @AuthenticationPrincipal Jwt jwt) {
        return RestaurantDto.buildFromEntity(this.restaurantService.updateRestaurant(id, editRestaurantDto, this.userService.isAdmin(jwt)));
    }
}


/* Notes

    J'ai pensé d'abord mettre les throw AccessDeniedException ici (si l'utilisateur connecté n'est pas un admin),
    mais je me suis dit que pour les tests unitaires, c'est mieux de faire cette vérification dans RestaurantService.

    Aussi, je n'ai pas mis l'upload de l'image dans le même formulaire que pour la création d'un restaurant,
    car c'était trop compliqué, j'ai eu trop de problèmes :').
*/