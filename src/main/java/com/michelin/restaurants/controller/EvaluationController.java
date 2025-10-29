package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
@Slf4j
public class EvaluationController {
    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // Ajoute une évaluation sur un restaurant
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    @Operation(summary = "Ajoute une évaluation sur un restaurant")
    public EvaluationDto addEvaluation(@Valid @RequestBody EvaluationDto evaluationDto, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaimAsString("name");
        return EvaluationDto.buildFromEntity(this.evaluationService.addEvaluation(evaluationDto, author));
    }

    // Supprime une évaluation
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une évaluation")
    public EvaluationDto deleteEvaluation(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaimAsString("name");
        return this.evaluationService.deleteEvaluation(id, author);
    }

    // Récupère les évaluations en fonction d'un (ou plusieurs) mots-clés
    @PostMapping("/search")
    @Operation(summary = "Récupère en fonction d'un (ou plusieurs) mots-clés")
    public List<EvaluationDto> getEvaluationsByKeywords(@RequestBody List<String> keywords) {
        return evaluationService.getEvaluationsByKeywords(keywords);
    }

    // L'utilisateur connecté récupère ses propres évaluations
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-evaluations")
    @Operation(summary = "L'utilisateur connecté récupère ses propres évaluations")
    public List<EvaluationDto> getMyEvaluations(@AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaimAsString("name");
        return this.evaluationService.getEvaluationsByAuthor(author)
                .stream()
                .map(EvaluationDto::buildFromEntity)
                .toList();
    }


    // Route non demandée, mais utile pour tester
    @GetMapping("/{restaurantId}")
    @Operation(summary = "Route non demandée: récupère tous les évaluations d'un restaurant, par son identifiant")
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
- La possibilité pour un utilisateur de récupérer toutes les évaluations qu'il a lui-même créées.

Les cas d'erreur doivent être gérés pour retourner une erreur (404, 500, etc) contenant :
- Un code
- Un message expliquant l'erreur

*/