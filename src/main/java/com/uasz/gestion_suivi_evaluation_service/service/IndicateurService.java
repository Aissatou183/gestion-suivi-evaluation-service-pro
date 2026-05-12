package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.LivrableClient;
import com.uasz.gestion_suivi_evaluation_service.dto.*;
import com.uasz.gestion_suivi_evaluation_service.entity.SuiviProjet;
import com.uasz.gestion_suivi_evaluation_service.repository.SuiviProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicateurService {

    private final SuiviProjetRepository suiviRepository;
    private final LivrableClient livrableClient;

    public IndicateurResponse calculer(Long encadrementId, String token) {

        List<LivrableResponse> livrables = livrableClient.livrablesParEncadrement(encadrementId, token);

        int nombreLivrables = livrables.size();

        int valides = (int) livrables.stream()
                .filter(l -> "VALIDE".equals(l.getStatut()) || "EVALUE".equals(l.getStatut()))
                .count();

        int retards = (int) livrables.stream()
                .filter(l -> "EN_RETARD".equals(l.getStatut()))
                .count();

        double moyenne = livrables.stream()
                .filter(l -> l.getNote() != null)
                .mapToInt(LivrableResponse::getNote)
                .average()
                .orElse(0.0);

        SuiviProjet dernierSuivi = suiviRepository
                .findFirstByEncadrementIdOrderByDateSuiviDesc(encadrementId)
                .orElse(null);

        int avancement = dernierSuivi == null ? 0 : dernierSuivi.getAvancementPourcentage();

        String risque = dernierSuivi == null ? "NON DEFINI" : dernierSuivi.getNiveauRisque();

        String statutProjet;
        if (avancement >= 100) {
            statutProjet = "TERMINE";
        } else if (retards > 0 || "ELEVE".equals(risque)) {
            statutProjet = "A_SURVEILLER";
        } else {
            statutProjet = "EN_COURS";
        }

        return IndicateurResponse.builder()
                .encadrementId(encadrementId)
                .avancementActuel(avancement)
                .nombreSuivis((int) suiviRepository.countByEncadrementId(encadrementId))
                .nombreLivrables(nombreLivrables)
                .livrablesValides(valides)
                .livrablesEnRetard(retards)
                .moyenneLivrables(Math.round(moyenne * 100.0) / 100.0)
                .niveauRisque(risque)
                .statutProjet(statutProjet)
                .build();
    }
}