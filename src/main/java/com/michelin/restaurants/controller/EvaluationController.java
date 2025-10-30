package com.michelin.restaurants.controller;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.service.EvaluationService;
import com.michelin.restaurants.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Évaluations", description = "Endpoints pour la gestion des évaluations")
@RestController
@RequestMapping("/evaluation")
@Slf4j
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final UserService userService;

    public EvaluationController(EvaluationService evaluationService, UserService userService) {
        this.evaluationService = evaluationService;
        this.userService = userService;
    }

    // Ajoute une évaluation sur un restaurant
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    @Operation(summary = "Ajoute une évaluation sur un restaurant (Utilisateur connecté)")
    public EvaluationDto addEvaluation(@Valid @RequestBody EvaluationDto evaluationDto, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaimAsString("name");
        return EvaluationDto.buildFromEntity(this.evaluationService.addEvaluation(evaluationDto, author));
    }

    // Supprime une évaluation
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une évaluation (Utilisateur connecté ou Administrateur)")
    public EvaluationDto deleteEvaluation(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String author = jwt.getClaimAsString("name");
        boolean isAdmin = this.userService.isAdmin(jwt);
        return EvaluationDto.buildFromEntity(this.evaluationService.deleteEvaluation(id, author, isAdmin));
    }

    // Récupère les évaluations en fonction d'un (ou plusieurs) mots-clés
    @PostMapping("/search")
    @Operation(summary = "Récupère les évaluations en fonction d'un (ou plusieurs) mots-clés")
    public List<EvaluationDto> getEvaluationsByKeywords(@RequestBody List<String> keywords) {
        return evaluationService.getEvaluationsByKeywords(keywords);
    }

    // L'utilisateur connecté récupère ses propres évaluations
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-evaluations")
    @Operation(summary = "Récupère ses propres évaluations (Utilisateur connecté)")
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