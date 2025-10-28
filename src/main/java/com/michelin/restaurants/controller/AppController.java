package com.michelin.restaurants.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    // J'ai décidé que la route par défaut redirige vers Swagger, pour aller plus vite
    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
