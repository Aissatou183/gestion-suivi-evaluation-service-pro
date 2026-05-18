package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.IndicateurResponse;
import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import com.uasz.gestion_suivi_evaluation_service.service.IndicateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indicateurs")
@RequiredArgsConstructor
public class IndicateurController {

    private final IndicateurService indicateurService;
    private final JwtService jwtService;

    private String token(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT manquant ou invalide.");
        }

        return authorizationHeader.substring(7);
    }

    private Long userId(String jwt) {
        return jwtService.extractUserId(jwt);
    }

    private String role(String jwt) {
        return jwtService.extractRole(jwt);
    }

    @GetMapping("/{encadrementId}")
    public IndicateurResponse calculer(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwt = token(authorizationHeader);

        return indicateurService.calculer(
                encadrementId,
                userId(jwt),
                role(jwt),
                jwt
        );
    }
}