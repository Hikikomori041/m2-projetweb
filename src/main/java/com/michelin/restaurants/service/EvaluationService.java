package com.michelin.restaurants.service;

import com.michelin.restaurants.client.EvaluationMinioClient;
import com.michelin.restaurants.dto.EvaluationDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.EvaluationRepository;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;

    private final EvaluationIndexService evaluationIndexService;

    private final EvaluationMinioClient minioClient;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository, RestaurantRepository restaurantRepository, EvaluationIndexService evaluationIndexService, EvaluationMinioClient minioClient) {
        this.evaluationRepository = evaluationRepository;
        this.restaurantRepository = restaurantRepository;
        this.evaluationIndexService = evaluationIndexService;
        this.minioClient = minioClient;
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

        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(id).get();
        if (evaluationEntity.getAuthor().equals(author) || isAdmin) {
            this.evaluationRepository.deleteById(id);

            // Suppression de l’index correspondant
            this.evaluationIndexService.deleteEvaluation(id.toString());

            // On supprime les photos
            List<Long> photosIds = evaluationEntity.getPhotosIds(); // on récupère la liste des id de photos
            for(Long photoId : photosIds) {
                String photoName = "blachere.evaluation_" + evaluationEntity.getId() + "_photo_" + photoId; // On récupère le nom de l'image uploadée
                // Ensuite, on supprime ladite image
                this.deleteEvaluationPhoto(photoName);
            }

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

    public List<String> getAllEvaluationPhotos(Long evaluationId) {
        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(evaluationId).get();
        List<Long> photosIds = evaluationEntity.getPhotosIds();
        List<String> photoUrls = new ArrayList<>();
        for(Long photoId : photosIds) {
            String photoName = "blachere.evaluation_" + evaluationEntity.getId() + "_photo_" + photoId;
            photoUrls.add(this.getEvaluationsPhoto(photoName));
        }
        return photoUrls;
    }

    // Pour upload une ou plusieurs photos de plats
    public List<String> addPhotos(Long evaluationId, List<MultipartFile> photos, String author) {
        EvaluationEntity evaluationEntity = this.evaluationRepository.findById(evaluationId).get();
        if (!evaluationEntity.getAuthor().equals(author)) {
            throw new AccessDeniedException("Vous ne pouvez ajouter d'image que sur vos propres évaluations !");
        }

        List<String> photosUrls = new ArrayList<>();

        for (MultipartFile photo : photos) {
            List<Long> photosIds = evaluationEntity.getPhotosIds(); // on récupère la liste des id de photos
            if (photosIds == null) photosIds = new ArrayList<>();

            Long photoId = System.currentTimeMillis(); // on génère un nouvel id
            photosIds.add(photoId); // on l'ajoute à la liste
            evaluationEntity.setPhotosIds(photosIds); // on enregistre la nouvelle liste
            this.evaluationRepository.save(evaluationEntity); // on enregistre l'évaluation avec la nouvelle liste de photos

            // Ensuite, on upload l'image avec l'id de l'évaluation et de la photo
            String photoName = "blachere.evaluation_" + evaluationId + "_photo_" + photoId; // nom de l'image uploadée
            photosUrls.add(this.uploadEvaluationPhoto(photoName, photo)); // On renvoie une URL pour voir l'image
        }
        return photosUrls;
    }




// =========================
// Sous-fonctions

    // Récupère une photo via son nom
    private String getEvaluationsPhoto(String photoName) {
        return minioClient.getEvaluationPhotoUrl(photoName);
    }

    // Upload une image liée à un restaurant
    private String uploadEvaluationPhoto(String photoName, MultipartFile image) {
        try {
            // 1 - Générer l'URL pré-signée
            String uploadUrl = this.minioClient.getEvaluationPhotoUpdateUrl(photoName);

            // 2 - Envoyer le fichier à cette URL
            HttpURLConnection connection = (HttpURLConnection) new URL(uploadUrl).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", image.getContentType());

            try (OutputStream os = connection.getOutputStream(); InputStream is = image.getInputStream()) {
                is.transferTo(os);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Erreur upload MinIO: code HTTP " + responseCode);
            }

            return this.minioClient.getEvaluationPhotoUrl(photoName);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image du restaurant : " + e.getMessage(), e);
        }
    }

    // Supprime une photo liée à une évaluation
    private void deleteEvaluationPhoto(String photoName) {
        try {
            // 1 - URL de suppression pré-signée
            String deleteUrl = this.minioClient.getEvaluationPhotoDeleteUrl(photoName);

            // 2 - Envoi de la requête DELETE
            HttpURLConnection connection = (HttpURLConnection) new URL(deleteUrl).openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                throw new RuntimeException("Erreur suppression MinIO : HTTP " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de la photo : " + e.getMessage(), e);
        }
    }
}

/* Note

    Je ne pense pas que la majeure partie du code des fonctions uploadEvaluationPhoto et deleteEvaluationPhoto
    est vraiment censée se trouver dans le Service, mais c'est plus simple pour moi de faire comme ça 🤷‍♂️.
 */