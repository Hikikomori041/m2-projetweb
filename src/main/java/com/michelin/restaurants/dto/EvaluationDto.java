package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.EvaluationEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record EvaluationDto(
    Long restaurantId,
    @NotBlank @Length(max = 50) String author,
    @NotBlank @Length(max = 255) String comment,
    @Min(0) @Max(3) Integer note,
    List<String> photosUrls
) {
    public static EvaluationDto buildFromEntity(EvaluationEntity evaluationEntity) {
        return new EvaluationDto(
                evaluationEntity.getRestaurant().getId(),
                evaluationEntity.getAuthor(),
                evaluationEntity.getComment(),
                evaluationEntity.getNote(),
                evaluationEntity.getPhotosUrls()
        );
    }
}

/*
Une évaluation est caractérisée par :

- Un identifiant unique (un nombre entier positif)
- Le nom de l'évaluateur (longueur max de 50 caractères)
- Le commentaire (longueur max de 255 caractères)
- Le nombre d'étoiles recommandé (0, 1, 2 ou 3) appellée "note"
- Une ou plusieurs photo des plats
 */