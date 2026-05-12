package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.SuiviRequest;
import com.uasz.gestion_suivi_evaluation_service.dto.SuiviResponse;
import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import com.uasz.gestion_suivi_evaluation_service.service.SuiviService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suivi")
@RequiredArgsConstructor
public class SuiviController {

    private final SuiviService suiviService;
    private final JwtService jwtService;

    private String token(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return "";
        }
        return auth.substring(7);
    }

    @PostMapping
    public SuiviResponse creer(
            @Valid @RequestBody SuiviRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        String token = token(authorization);

        return suiviService.creer(
                request,
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token)
        );
    }

    @GetMapping("/encadrement/{encadrementId}")
    public List<SuiviResponse> parEncadrement(@PathVariable Long encadrementId) {
        return suiviService.parEncadrement(encadrementId);
    }
}