package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public EvaluationEntity addEvaluation(EvaluationDto evaluationDto) {
        return this.evaluationRepository.save(EvaluationEntity.buildFromDto(evaluationDto));
    }

    public EvaluationDto deleteEvaluation(Long id) {
        if(!this.evaluationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'évaluation avec l'identifiant '" + id + "' n'existe pas !");
        }

        //todo: supprimer les photos

        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(id).get();
        this.evaluationRepository.deleteById(id);

        return EvaluationDto.buildFromEntity(evaluationEntity);
    }

    public List<EvaluationDto> getEvaluationsByKeywords(List<String> keywords) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        //todo: récupérer la liste des évaluations par mots-clés (index ?)

        return evaluationDtos;
    }
}
