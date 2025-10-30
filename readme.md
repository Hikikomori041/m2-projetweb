# Présentation
Dépôt du projet de cours de 2025 de Service Web du Master 2 Informatique, à la Faculté des Sciences et Technologies, Université de Lorraine.

# Auteur
Nicolas BLACHÈRE

# Explication de l'arborescence de fichiers
## `main/...`
### `/config`
#### `SecurityConfig`
Fichier essentiel aux connections oauth2.
#### `SwaggerConfig`
La configuration de base de l'UI Swagger de notre API.
#### `TrailingSlashInterceptor & WebConfig`
Ces fichiers ne sont là que pour supprimer le dernier "/" qui serait potentiellement écrit sur une route.
Par exemple, "/restaurant/" devient "/restaurant". Ce n'était pas demandé, mais je me suis fait plaisir.

### `/controller`
#### `AppController`
Ce fichier n'existe que pour rediriger la route "/" vers "/swagger-ui/index.html" pour gagner du temps.
#### `CustomExceptionHandler`
Ce fichier permet de réagir à certaines exceptions levées par les routes et de renvoyer une réponse adaptée à l'utilisateur.
#### `EvaluationController`
Les routes concernant les évaluations.
#### `RestaurantController`
Les routes concernant les restaurants.

### `/dto`
#### `EditRestaurantDto`
Pour passer le nouveau nom et la nouvelle adresse d'un restaurant à modifier.
#### `EvaluationDto`
Les informations sur une évaluation qui est retournée par certaines routes d'évaluations.
#### `FullRestaurantDto`
Pour retourner la moyenne d'un restaurant, suivi du restaurant lui-même.
#### `RestaurantDto`
Les informations sur un restaurant qui est retourné par certaines routes de restaurants.

### `/entity`
#### `EvaluationEntity`
Ce que l'on stocke d'une évaluation en base de données.
#### `RestaurantEntity`
Ce que l'on stocke d'un restaurant en base de données.

### `/repository`
#### `EvaluationRepository`
Permet la connexion des évaluations à la base de données. Possède aussi deux méthodes abstraites pour récupérer toutes les évaluations d'un auteur ou d'un restaurant.
#### `RestaurantRepository`
Permet la connexion des restaurants à la base de données.

### `/service`
#### `EvaluationIndexService`
Pour indexer les évaluations (utile pour la recherche par mots-clés)
#### `EvaluationService`
Les actions réalisées sur les évaluations.
#### `RestaurantService`
Les actions réalisées sur les restaurants.
#### `UserService`
Ne sert qu'à identifier si un utilisateur connecté possède le rôle administrateur.

### `/resources`
#### `/static/favicon.ico`
Je me suis fait plaisir en mettant une icône de Michelin pour le RP (:
## `test/...`
#### `EvaluationServiceTest`
Fichier contenant les tests unitaires concernant les actions effectuées sur les évaluations.
#### `RestaurantServiceTest`
Fichier contenant les tests unitaires concernant les actions effectuées sur les restaurants.