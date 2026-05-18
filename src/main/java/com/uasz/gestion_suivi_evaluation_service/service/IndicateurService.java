package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.EncadrementClient;
import com.uasz.gestion_suivi_evaluation_service.client.LivrableClient;
import com.uasz.gestion_suivi_evaluation_service.dto.EncadrementResponse;
import com.uasz.gestion_suivi_evaluation_service.dto.IndicateurResponse;
import com.uasz.gestion_suivi_evaluation_service.dto.LivrableResponse;
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
    private final EncadrementClient encadrementClient;

    public IndicateurResponse calculer(
            Long encadrementId,
            Long userId,
            String role,
            String token
    ) {
        verifierAcces(encadrementId, userId, role, token);

        List<LivrableResponse> livrables =
                livrableClient.livrablesParEncadrement(encadrementId, token);

        int nombreLivrables = livrables.size();

        int livrablesValides = (int) livrables.stream()
                .filter(l ->
                        "VALIDE".equalsIgnoreCase(l.getStatut())
                                || "EVALUE".equalsIgnoreCase(l.getStatut())
                )
                .count();

        int livrablesEnRetard = (int) livrables.stream()
                .filter(l -> "EN_RETARD".equalsIgnoreCase(l.getStatut()))
                .count();

        double moyenneLivrables = livrables.stream()
                .filter(l -> l.getNote() != null)
                .mapToInt(LivrableResponse::getNote)
                .average()
                .orElse(0.0);

        SuiviProjet dernierSuivi = suiviRepository
                .findFirstByEncadrementIdOrderByDateSuiviDesc(encadrementId)
                .orElse(null);

        int avancementActuel = dernierSuivi == null
                ? 0
                : safeInt(dernierSuivi.getAvancementPourcentage());

        String niveauRisque = dernierSuivi == null
                ? "NON_DEFINI"
                : safeString(dernierSuivi.getNiveauRisque(), "NON_DEFINI");

        String statutProjet = calculerStatutProjet(
                avancementActuel,
                livrablesEnRetard,
                niveauRisque
        );

        return IndicateurResponse.builder()
                .encadrementId(encadrementId)
                .avancementActuel(avancementActuel)
                .nombreSuivis((int) suiviRepository.countByEncadrementId(encadrementId))
                .nombreLivrables(nombreLivrables)
                .livrablesValides(livrablesValides)
                .livrablesEnRetard(livrablesEnRetard)
                .moyenneLivrables(arrondir(moyenneLivrables))
                .niveauRisque(niveauRisque)
                .statutProjet(statutProjet)
                .build();
    }

    private String calculerStatutProjet(
            int avancement,
            int retards,
            String risque
    ) {
        if (avancement >= 100) {
            return "TERMINE";
        }

        if (retards > 0 || "ELEVE".equalsIgnoreCase(risque)) {
            return "A_SURVEILLER";
        }

        if ("MOYEN".equalsIgnoreCase(risque)) {
            return "EN_ATTENTION";
        }

        return "EN_COURS";
    }

    private void verifierAcces(
            Long encadrementId,
            Long userId,
            String role,
            String token
    ) {
        String roleNettoye = normaliserRole(role);

        EncadrementResponse encadrement =
                encadrementClient.trouverParId(encadrementId, token);

        if (encadrement == null) {
            throw new RuntimeException("Encadrement introuvable.");
        }

        if ("ADMINISTRATEUR".equals(roleNettoye)) {
            return;
        }

        if ("ENSEIGNANT".equals(roleNettoye)) {
            if (encadrement.getEnseignantId() != null
                    && encadrement.getEnseignantId().equals(userId)) {
                return;
            }

            throw new RuntimeException(
                    "Accès refusé : vous n'êtes pas encadreur de ce projet."
            );
        }

        if ("ETUDIANT".equals(roleNettoye)) {
            if (encadrement.getEtudiantId() != null
                    && encadrement.getEtudiantId().equals(userId)) {
                return;
            }

            throw new RuntimeException(
                    "Accès refusé : ce projet ne vous appartient pas."
            );
        }

        throw new RuntimeException("Rôle non autorisé.");
    }

    private String normaliserRole(String role) {
        if (role == null) {
            return "";
        }

        return role
                .replace("ROLE_", "")
                .trim()
                .toUpperCase();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String safeString(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value;
    }

    private double arrondir(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}