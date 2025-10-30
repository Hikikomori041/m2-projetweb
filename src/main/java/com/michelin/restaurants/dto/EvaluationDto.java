package com.michelin.restaurants.dto;

import com.michelin.restaurants.entity.EvaluationEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record EvaluationDto(
    @NotNull Long restaurantId,
    @Length(max = 50) String author,
    @NotBlank @Length(max = 255) String comment,
    @NotNull @Min(0) @Max(3) Integer note,
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