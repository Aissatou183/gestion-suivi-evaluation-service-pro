package com.uasz.gestion_suivi_evaluation_service.controller;

import com.uasz.gestion_suivi_evaluation_service.dto.HistoriqueResponse;
import com.uasz.gestion_suivi_evaluation_service.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    @GetMapping("/encadrement/{encadrementId}")
    public List<HistoriqueResponse> parEncadrement(@PathVariable Long encadrementId) {
        return historiqueService.parEncadrement(encadrementId);
    }
}