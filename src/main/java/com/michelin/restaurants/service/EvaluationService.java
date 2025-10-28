package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository, RestaurantRepository restaurantRepository) {
        this.evaluationRepository = evaluationRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public EvaluationEntity addEvaluation(EvaluationDto evaluationDto) {
        RestaurantEntity restaurantEntity = this.restaurantRepository.findById(evaluationDto.restaurantId())
                .orElseThrow( () -> new NoSuchElementException("Le restaurant avec l'id " + evaluationDto.restaurantId() + " n'a pas été trouvé."));

        return this.evaluationRepository.save(EvaluationEntity.buildFromDto(evaluationDto, restaurantEntity));
    }

    public EvaluationDto deleteEvaluation(Long id) {
        if(!this.evaluationRepository.existsById(id)) {
            throw new NoSuchElementException("L'évaluation avec l'identifiant '" + id + "' n'existe pas !");
        }

        //todo: supprimer les photos

        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(id).get();
        this.evaluationRepository.deleteById(id);

        return EvaluationDto.buildFromEntity(evaluationEntity);
    }

    public List<EvaluationDto> getEvaluationsByKeywords(List<String> keywords) {
        List<EvaluationDto> evaluations = new ArrayList<>();

        //todo: récupérer la liste des évaluations par mots-clés (index ?)

        return evaluations;
    }

    public List<EvaluationEntity> getEvaluationsByRestaurantId(Long restaurantId) {
        if(!this.restaurantRepository.existsById(restaurantId)) {
            throw new NoSuchElementException("Le restaurant avec l'identifiant '" + restaurantId + "' n'existe pas");
        }
        return this.evaluationRepository.findAllByRestaurantId(restaurantId);
    }
}
