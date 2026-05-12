package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.IndicateurResponse;
import com.uasz.gestion_suivi_evaluation_service.service.IndicateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indicateurs")
@RequiredArgsConstructor
public class IndicateurController {

    private final IndicateurService indicateurService;

    private String token(String auth) {
        return auth == null ? "" : auth.replace("Bearer ", "");
    }

    @GetMapping("/{encadrementId}")
    public IndicateurResponse calculer(
            @PathVariable Long encadrementId,
            @RequestHeader("Authorization") String authorization
    ) {
        return indicateurService.calculer(encadrementId, token(authorization));
    }
}