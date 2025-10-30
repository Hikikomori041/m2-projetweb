package com.michelin.restaurants.service;

import com.michelin.restaurants.client.RestaurantMinioClient;
import com.michelin.restaurants.dto.RestaurantDto;
import com.michelin.restaurants.dto.FullRestaurantDto;
import com.michelin.restaurants.entity.EvaluationEntity;
import com.michelin.restaurants.entity.RestaurantEntity;
import com.michelin.restaurants.repository.RestaurantRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMinioClient minioClient;

    public RestaurantService(RestaurantRepository restaurantRepository, RestaurantMinioClient minioClient) {
        this.restaurantRepository = restaurantRepository;
        this.minioClient = minioClient;
    }


    // Retourne la liste complète des restaurants, avec la moyenne des notes de leurs évaluations (-1 si aucune)
    public List<FullRestaurantDto> getAllFullRestaurants() {
        List<RestaurantEntity> allRestaurants = this.getAllRestaurants();

        List<FullRestaurantDto> fullRestaurants = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            fullRestaurants.add(new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity)));
        }

        return fullRestaurants;
    }

    // Retourne un restaurant via son identifiant, avec la moyenne des notes de ses évaluations (-1 si aucune)
    public FullRestaurantDto getFullRestaurantById(Long id) {
        RestaurantEntity restaurantEntity = getRestaurantById(id);

        return new FullRestaurantDto(getRestaurantAverage(restaurantEntity), RestaurantDto.buildFromEntity(restaurantEntity));
    }

    // Ajoute un restaurant (Administrateur seulement)
    public RestaurantEntity addRestaurant(RestaurantDto restaurantDto, boolean isAdmin) {
        if (!isAdmin) throw new AccessDeniedException("Vous devez être administrateur pour ajouter un restaurant !");

        return this.restaurantRepository.save(RestaurantEntity.buildFromDto(restaurantDto));
    }

    public String addRestaurantImage(Long id, MultipartFile image, boolean isAdmin) throws Exception {
        if (!isAdmin) throw new AccessDeniedException("Vous devez être administrateur pour ajouter une image à un restaurant !");

        if (image == null) throw new Exception("Aucune image n'a été envoyée.");

        String imageUrl = uploadRestaurantImage(id, image);
        System.out.println("Restaurant image can be downloaded at:" + imageUrl);

        return imageUrl;
    }

    public String getRestaurantImage(Long id) {
        return getRestaurantImageUrl(id);
    }

    // Met à jour le nom et l'adresse d'un restaurant (Administrateur seulement)
    public RestaurantEntity updateRestaurant(Long id, RestaurantDto restaurantDto, boolean isAdmin) {
        if (!isAdmin) throw new AccessDeniedException("Vous devez être administrateur pour modifier un restaurant !");
        if(!this.restaurantRepository.existsById(id)) throw new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas");

        RestaurantEntity restaurantEntity = this.getRestaurantById(id);
        restaurantEntity.setName(restaurantDto.name());
        restaurantEntity.setAddress(restaurantDto.address());

        return this.restaurantRepository.save(restaurantEntity);
    }


//======================
// Sous-fonctions

    public List<RestaurantEntity> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurantById(Long id) {
        return this.restaurantRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Le restaurant avec l'identifiant '" + id + "' n'existe pas"));
    }

    public float getRestaurantAverage(RestaurantEntity restaurantEntity) {
        float moyenne = -1;
        List<EvaluationEntity> evaluation = restaurantEntity.getEvaluations();
        if (!evaluation.isEmpty()) {
            int somme = 0;
            int nbEvaluations = evaluation.size();
            for (EvaluationEntity evaluationEntity : evaluation) {
                somme += evaluationEntity.getNote();
            }
            moyenne = 1.0f * somme / nbEvaluations; // 1.0f permet de convertir le résultat en float
            moyenne = Math.round(moyenne * 100f) / 100f; // pour ne garder que 2 chiffres après la virgule
        }
        return moyenne;
    }

    // Renvoie une URL pour voir l'image d'un restaurant
    private String getRestaurantImageUrl(Long id) {
        return minioClient.getRestaurantImageUrl(id.toString());
    }

    // Upload une image liée à un restaurant
    private String uploadRestaurantImage(Long id, MultipartFile image) {
        try {
            // 1 - Générer l'URL pré-signée
            String uploadUrl = this.minioClient.getRestaurantImageUpdateUrl(id.toString());

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

            return this.minioClient.getRestaurantImageUrl(id.toString());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image du restaurant : " + e.getMessage(), e);
        }
    }

    // Supprime l'image liée à un restaurant (non utilisée, mais serait utile pour une route où on supprime un restaurant)
    private void deleteRestaurantImage(Long id) {
        try {
            // 1 - URL de suppression pré-signée
            String deleteUrl = this.minioClient.getRestaurantImageDeleteUrl(id.toString());

            // 2 - Envoi de la requête DELETE
            HttpURLConnection connection = (HttpURLConnection) new URL(deleteUrl).openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode != 204 && responseCode != 200) {
                throw new RuntimeException("Erreur suppression MinIO : HTTP " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'image du restaurant : " + e.getMessage(), e);
        }
    }
}

/* Note

    Je ne pense pas que la majeure partie du code des fonctions uploadRestaurantImage et deleteRestaurantImage
    est vraiment censée se trouver dans le Service, mais c'est plus simple pour moi de faire comme ça 🤷‍♂️.
 */