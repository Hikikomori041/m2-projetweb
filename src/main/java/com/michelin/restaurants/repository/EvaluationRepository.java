package com.michelin.restaurants.repository;

import com.michelin.restaurants.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Long> {
    List<EvaluationEntity> findAllByRestaurantId(Long restaurantId);
    List<EvaluationEntity> findAllByAuthor(String author);
}
