package com.michelin.restaurants.entity;

import com.michelin.restaurants.dto.EvaluationDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity(name = "evaluation")
@Data
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "comment")
    private String comment;

    @Column(name = "note")
    private Integer note;

    @Column(name = "photos")
    private List<String> photosUrls;

    @ManyToOne
    @JoinColumn(name = "restaurant")
    private RestaurantEntity restaurant;


    public static EvaluationEntity buildFromDto(EvaluationDto evaluationDto) {
        var evaluationEntity = new EvaluationEntity();

        evaluationEntity.setAuthor(evaluationDto.author());
        evaluationEntity.setComment(evaluationDto.comment());
        evaluationEntity.setNote(evaluationDto.note());
        evaluationEntity.setPhotosUrls(evaluationDto.photosUrls());

        return evaluationEntity;
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