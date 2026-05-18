package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.HistoriqueResponse;
import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import com.uasz.gestion_suivi_evaluation_service.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;
    private final JwtService jwtService;

    /*
     * =========================================================
     * JWT
     * =========================================================
     */

    private String token(String authorizationHeader) {

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Token JWT manquant ou invalide."
            );
        }

        return authorizationHeader.substring(7);
    }

    private Long userId(String jwt) {
        return jwtService.extractUserId(jwt);
    }

    private String role(String jwt) {
        return jwtService.extractRole(jwt);
    }

    /*
     * =========================================================
     * HISTORIQUE PAR ENCADREMENT
     * =========================================================
     */

    @GetMapping("/encadrement/{encadrementId}")
    public List<HistoriqueResponse> parEncadrement(

            @PathVariable
            Long encadrementId,

            @RequestHeader("Authorization")
            String authorizationHeader
    ) {

        String jwt = token(authorizationHeader);

        return historiqueService.parEncadrement(
                encadrementId,
                userId(jwt),
                role(jwt),
                jwt
        );
    }
}