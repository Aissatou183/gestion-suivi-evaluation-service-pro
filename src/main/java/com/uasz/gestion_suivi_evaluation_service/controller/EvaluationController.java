package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.EvaluationRequest;
import com.uasz.gestion_suivi_evaluation_service.dto.EvaluationResponse;
import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import com.uasz.gestion_suivi_evaluation_service.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
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

    private String nomComplet(String jwt) {
        return jwtService.extractNomComplet(jwt);
    }

    private String role(String jwt) {
        return jwtService.extractRole(jwt);
    }

    /*
     * =========================================================
     * CREER EVALUATION
     * =========================================================
     */

    @PostMapping
    public EvaluationResponse creer(

            @Valid
            @RequestBody
            EvaluationRequest request,

            @RequestHeader("Authorization")
            String authorizationHeader
    ) {

        String jwt = token(authorizationHeader);

        return evaluationService.creer(
                request,
                userId(jwt),
                nomComplet(jwt),
                role(jwt),
                jwt
        );
    }

    /*
     * =========================================================
     * LISTE PAR ENCADREMENT
     * =========================================================
     */

    @GetMapping("/encadrement/{encadrementId}")
    public List<EvaluationResponse> parEncadrement(

            @PathVariable
            Long encadrementId,

            @RequestHeader("Authorization")
            String authorizationHeader
    ) {

        String jwt = token(authorizationHeader);

        return evaluationService.parEncadrement(
                encadrementId,
                userId(jwt),
                role(jwt),
                jwt
        );
    }
}