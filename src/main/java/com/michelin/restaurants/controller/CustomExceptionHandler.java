package com.michelin.restaurants.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "code", 404,
                "message", e.getMessage() != null ? e.getMessage() : "Ressource introuvable."
        ));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
//        assert ex.getReason() != null;
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "code", ex.getStatusCode().value(),
                "message", ex.getReason()
        ));
    }



    // 404 : route inexistante
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "code", 404,
                "message", "La route demandée est introuvable."
        ));
    }

    // 500 : erreur interne (toutes les autres exceptions)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "code", 500,
                "message", "Erreur interne du serveur."
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
}
