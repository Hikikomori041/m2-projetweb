package com.michelin.restaurants.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {
    public boolean isAdmin(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        List<String> clientRoles = List.of();

        if (resourceAccess != null && resourceAccess.containsKey("coursm2")) {
            Map<String, Object> coursm2 = (Map<String, Object>) resourceAccess.get("coursm2");
            clientRoles = (List<String>) coursm2.get("roles");
        }
        System.out.println("----------------------\n\tclientRoles: " + clientRoles);
        return clientRoles.contains("ADMIN");
    }
}

/* Note

Ce fichier contient les fonctions liées aux utilisateurs (bon ici, il n'y a qu'un test pour savoir s'il est admin).
 */
