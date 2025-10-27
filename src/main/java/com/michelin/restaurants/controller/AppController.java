package com.michelin.restaurants.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {
    @GetMapping
    public String index() {
        return "Vous avez demandé la page d'accueil de la nouvelle API de restaurants de Michelin.\nMais il n'y a rien à voir ici...";
    }
}
