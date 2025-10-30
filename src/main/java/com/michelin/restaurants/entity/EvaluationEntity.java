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


    public static EvaluationEntity buildFromDto(EvaluationDto evaluationDto, RestaurantEntity restaurantEntity) {
        var evaluationEntity = new EvaluationEntity();

        evaluationEntity.setAuthor(evaluationDto.author());
        evaluationEntity.setComment(evaluationDto.comment());
        evaluationEntity.setNote(evaluationDto.note());
        evaluationEntity.setPhotosUrls(evaluationDto.photosUrls());
        evaluationEntity.setRestaurant(restaurantEntity);

        return evaluationEntity;
    }
}