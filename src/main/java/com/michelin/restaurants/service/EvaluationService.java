package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    public EvaluationEntity addEvaluation(EvaluationDto evaluationDto, String author) {
        RestaurantEntity restaurantEntity = this.restaurantRepository.findById(evaluationDto.restaurantId())
                .orElseThrow( () -> new NoSuchElementException("Le restaurant avec l'id " + evaluationDto.restaurantId() + " n'a pas été trouvé."));

        EvaluationEntity evaluationEntity = EvaluationEntity.buildFromDto(evaluationDto, restaurantEntity);
        evaluationEntity.setAuthor(author); // On définit l'auteur de l'évaluation

        return this.evaluationRepository.save(evaluationEntity);
    }

    public EvaluationDto deleteEvaluation(Long id, String author) {
        if(!this.evaluationRepository.existsById(id)) {
            throw new NoSuchElementException("L'évaluation avec l'identifiant '" + id + "' n'existe pas !");
        }

        //todo: supprimer les photos

        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(id).get();
        if (evaluationEntity.getAuthor().equals(author)) {
            this.evaluationRepository.deleteById(id);
            return EvaluationDto.buildFromEntity(evaluationEntity);
        }
        //else
        throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres évaluations !");
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

    public List<EvaluationEntity> getEvaluationsByAuthor(String author) {
        return this.evaluationRepository.findAllByAuthor(author);
    }
}
