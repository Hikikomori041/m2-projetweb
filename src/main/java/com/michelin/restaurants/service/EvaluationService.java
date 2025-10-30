package com.michelin.restaurants.service;

import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;

    private final EvaluationIndexService evaluationIndexService;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository, RestaurantRepository restaurantRepository, EvaluationIndexService evaluationIndexService) {
        this.evaluationRepository = evaluationRepository;
        this.restaurantRepository = restaurantRepository;
        this.evaluationIndexService = evaluationIndexService;
    }

//    // Pour rebuild l'index avec les évaluations déjà ajoutées avant l'index, au démarrage de l'application
//    @PostConstruct
//    public void rebuildIndexAtStartup() {
//        List<EvaluationEntity> all = evaluationRepository.findAll();
//        for (EvaluationEntity e : all) {
//            evaluationIndexService.indexEvaluation(e.getId().toString(), e.getComment());
//        }
//    }


    // Ajoute une évaluation avec comme auteur le nom de l'utilisateur connecté
    public EvaluationEntity addEvaluation(EvaluationDto evaluationDto, String author) {
        RestaurantEntity restaurantEntity = this.restaurantRepository.findById(evaluationDto.restaurantId())
                .orElseThrow( () -> new NoSuchElementException("Le restaurant avec l'id " + evaluationDto.restaurantId() + " n'a pas été trouvé."));

        EvaluationEntity evaluationEntity = EvaluationEntity.buildFromDto(evaluationDto, restaurantEntity);
        evaluationEntity.setAuthor(author); // On définit l'auteur de l'évaluation

        EvaluationEntity savedEvaluation = this.evaluationRepository.save(evaluationEntity);
        // Indexation après sauvegarde
        this.evaluationIndexService.indexEvaluation(savedEvaluation.getId().toString(), savedEvaluation.getComment());

        return savedEvaluation;
    }

    // Supprime une évaluation via son id, si l'utilisateur connecté en est l'auteur ou est un admin
    public EvaluationEntity deleteEvaluation(Long id, String author, boolean isAdmin) {
        if(!this.evaluationRepository.existsById(id)) {
            throw new NoSuchElementException("L'évaluation avec l'identifiant '" + id + "' n'existe pas !");
        }

        //todo: supprimer les photos (si on fait des uploads, utiliser les routes DELETE)

        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(id).get();
        if (evaluationEntity.getAuthor().equals(author) || isAdmin) {
            this.evaluationRepository.deleteById(id);
            // Suppression de l’index correspondant
            this.evaluationIndexService.deleteEvaluation(id.toString());
            return evaluationEntity;
        }
        //else
        throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres évaluations !");
    }

    // Retourne la liste des évaluations contenant tel ou tel mot-clé (via les index)
    public List<EvaluationDto> getEvaluationsByKeywords(List<String> keywords) {
        String searchText = String.join(" ", keywords);
        List<String> matchingIds = this.evaluationIndexService.searchEvaluation(searchText);

        // Petite conversion d'une liste de String à une liste de Long
        List<Long> evaluationsIds = matchingIds.stream()
                .map(Long::valueOf)
                .toList();

        List<EvaluationEntity> entities = this.evaluationRepository.findAllById(evaluationsIds);
        return entities.stream()
                .map(EvaluationDto::buildFromEntity)
                .toList();
    }

    // Retourne la liste des évaluations faites par un auteur (via son nom)
    public List<EvaluationEntity> getEvaluationsByAuthor(String author) {
        return this.evaluationRepository.findAllByAuthor(author);
    }

    // Fonction non demandée, ajout personnel
    public List<EvaluationEntity> getEvaluationsByRestaurantId(Long restaurantId) {
        if(!this.restaurantRepository.existsById(restaurantId)) {
            throw new NoSuchElementException("Le restaurant avec l'identifiant '" + restaurantId + "' n'existe pas");
        }
        return this.evaluationRepository.findAllByRestaurantId(restaurantId);
    }
}
