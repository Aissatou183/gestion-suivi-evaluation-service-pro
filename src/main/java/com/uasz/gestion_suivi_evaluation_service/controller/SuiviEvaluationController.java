package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.*;
import com.uasz.gestion_suivi_evaluation_service.service.SuiviEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class SuiviEvaluationController {

    private final SuiviEvaluationService service;

    private String token(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return "";
        }
        return authorizationHeader.substring(7);
    }

    @PostMapping("/api/suivi")
    public SuiviProjetResponse creerSuivi(
            @Valid @RequestBody SuiviProjetRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return service.creerSuivi(request, token(authorizationHeader));
    }

    @PostMapping("/api/evaluations")
    public EvaluationProjetResponse evaluerProjet(
            @Valid @RequestBody EvaluationProjetRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return service.evaluerProjet(request, token(authorizationHeader));
    }

    @PostMapping("/api/historique")
    public HistoriqueActionResponse creerAction(
            @Valid @RequestBody HistoriqueActionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return service.creerAction(request, token(authorizationHeader));
    }

    @GetMapping("/api/indicateurs/{encadrementId}")
    public IndicateurProjetResponse indicateurs(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return service.indicateurs(encadrementId, token(authorizationHeader));
    }

    @GetMapping("/api/suivi/encadrement/{encadrementId}")
    public List<SuiviProjetResponse> suivisParEncadrement(@PathVariable Long encadrementId) {
        return service.suivisParEncadrement(encadrementId);
    }

    @GetMapping("/api/evaluations/encadrement/{encadrementId}")
    public List<EvaluationProjetResponse> evaluationsParEncadrement(@PathVariable Long encadrementId) {
        return service.evaluationsParEncadrement(encadrementId);
    }

    @GetMapping("/api/historique")
    public List<HistoriqueActionResponse> historiqueTous() {
        return service.historiqueTous();
    }

    @GetMapping("/api/historique/encadrement/{encadrementId}")
    public List<HistoriqueActionResponse> historiqueParEncadrement(@PathVariable Long encadrementId) {
        return service.historiqueParEncadrement(encadrementId);
    }
}
