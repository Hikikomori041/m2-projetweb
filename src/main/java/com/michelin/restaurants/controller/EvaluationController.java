package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // Ajoute une évaluation sur un restaurant
    @PostMapping()
    @Operation(summary = "Ajoute une évaluation sur un restaurant")
    public EvaluationDto addEvaluation(@Valid @RequestBody EvaluationDto evaluationDto) {
        return EvaluationDto.buildFromEntity(this.evaluationService.addEvaluation(evaluationDto));
    }

    // Supprime une évaluation
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une évaluation")
    public EvaluationDto deleteEvaluation(@PathVariable Long id) {
        return this.evaluationService.deleteEvaluation(id);
    }

    // Récupère les évaluations en fonction d'un (ou plusieurs) mots-clés
    @PostMapping("/search")
    @Operation(summary = "Récupère en fonction d'un (ou plusieurs) mots-clés")
    public List<EvaluationDto> getEvaluationsByKeywords(@RequestBody List<String> keywords) {
        return evaluationService.getEvaluationsByKeywords(keywords);
    }


    // Route test
    @GetMapping("/{restaurantId}")
    @Operation(summary = "Récupère tous les évaluations d'un restaurant, par son identifiant")
    public List<EvaluationDto> getEvaluationsByRestaurantId(@PathVariable Long restaurantId) {
        return this.evaluationService.getEvaluationsByRestaurantId(restaurantId)
                .stream()
                .map(EvaluationDto::buildFromEntity)
                .toList();
    }

}


/*
Evaluations
- La possibilité d'ajouter une évaluation sur un restaurant
- La possibilité de supprimer une évaluation
- La possibilité de récupérer les évaluations en fonction d'un (ou plusieurs) mots-clés
- La possibilité pour un utilisateur de récupérer toutes les évaluations qu'il a lui-même créé.

Les routes retournant un (ou plusieurs) restaurant (marquées par *) doivent aussi retourner la moyenne des notes du-dit restaurant dans une propriété nommée "moyenne". Si le restaurant ne dispose d'aucune evaluation, la moyenne est de -1.

Les cas d'erreur doivent être gérés pour retourner une erreur (404, 500, etc) contenant :
- Un code
- Un message expliquant l'erreur

*/