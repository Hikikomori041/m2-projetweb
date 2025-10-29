package com.michelin.restaurants.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomExceptionHandler {

    // 403 : erreur de droits d'accès
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "code", 403,
                "message", e.getMessage() != null ? e.getMessage() : "Accès refusé."
        ));
    }

    // 404 : route inexistante
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException e) {
        return erreur404(e);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException e) {
        return erreur404(e);
    }

    // 500 : erreur interne (toutes les autres exceptions)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(Exception e) {
        return erreur500(e);
    }


    // Pour afficher l'erreur 404.
    private ResponseEntity<Map<String, Object>> erreur404(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "code", 404,
                "message", e.getMessage() != null ? e.getMessage() : "La route demandée est introuvable."
        ));
    }
    // Pareil pour l'erreur 500
    private ResponseEntity<Map<String, Object>> erreur500(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "code", 500,
                "message", e.getMessage() != null ? e.getMessage() : "Erreur interne du serveur."
        ));
    }

    // Pour mieux expliquer les erreurs de champs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> switch (error.getField()) {
                    case "note" -> "La note de l'évaluation doit être comprise entre 0 et 3 étoiles.";
                    default -> error.getField() + " invalide.";
                })
                .findFirst()
                .orElse("Erreur de validation des champs.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "code", 400,
                "message", message
        ));
    }

    // Ça je sais plus à quoi ça sert honnêtement
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "code", ex.getStatusCode().value(),
                "message", ex.getReason() != null ? ex.getReason() : ""
        ));
    }
}
