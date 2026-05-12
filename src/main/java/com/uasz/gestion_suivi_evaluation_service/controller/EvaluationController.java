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

    private String token(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return "";
        }
        return auth.substring(7);
    }

    @PostMapping
    public EvaluationResponse creer(
            @Valid @RequestBody EvaluationRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = token(authorization);

        return evaluationService.creer(
                request,
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token)
        );
    }

    @GetMapping("/encadrement/{encadrementId}")
    public List<EvaluationResponse> parEncadrement(@PathVariable Long encadrementId) {
        return evaluationService.parEncadrement(encadrementId);
    }
}