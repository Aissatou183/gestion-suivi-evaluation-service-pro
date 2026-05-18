package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.EncadrementClient;
import com.uasz.gestion_suivi_evaluation_service.dto.EncadrementResponse;
import com.uasz.gestion_suivi_evaluation_service.dto.HistoriqueResponse;
import com.uasz.gestion_suivi_evaluation_service.entity.HistoriqueAction;
import com.uasz.gestion_suivi_evaluation_service.repository.HistoriqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;
    private final EncadrementClient encadrementClient;

    public void ajouter(
            Long encadrementId,
            Long acteurId,
            String acteurNomComplet,
            String acteurRole,
            String action,
            String titre,
            String description
    ) {
        if (encadrementId == null) {
            throw new RuntimeException("L'encadrement est obligatoire pour l'historique.");
        }

        HistoriqueAction historique = HistoriqueAction.builder()
                .encadrementId(encadrementId)
                .acteurId(acteurId)
                .acteurNomComplet(nettoyer(acteurNomComplet))
                .acteurRole(normaliserRole(acteurRole))
                .action(nettoyer(action))
                .titre(nettoyer(titre))
                .description(nettoyer(description))
                .dateAction(LocalDateTime.now())
                .build();

        historiqueRepository.save(historique);
    }

    public List<HistoriqueResponse> parEncadrement(
            Long encadrementId,
            Long userId,
            String role,
            String token
    ) {
        verifierAcces(encadrementId, userId, role, token);

        return lister(encadrementId);
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

            throw new RuntimeException("Accès refusé : vous n'êtes pas encadreur de ce projet.");
        }

        if ("ETUDIANT".equals(roleNettoye)) {
            if (encadrement.getEtudiantId() != null
                    && encadrement.getEtudiantId().equals(userId)) {
                return;
            }

            throw new RuntimeException("Accès refusé : ce projet ne vous appartient pas.");
        }

        throw new RuntimeException("Rôle non autorisé.");
    }

    private List<HistoriqueResponse> lister(Long encadrementId) {
        return historiqueRepository
                .findByEncadrementIdOrderByDateActionDesc(encadrementId)
                .stream()
                .map(this::map)
                .toList();
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

    private String nettoyer(String valeur) {
        if (valeur == null) {
            return "";
        }

        return valeur.trim();
    }

    private HistoriqueResponse map(HistoriqueAction h) {
        return HistoriqueResponse.builder()
                .id(h.getId())
                .encadrementId(h.getEncadrementId())
                .acteurId(h.getActeurId())
                .acteurNomComplet(h.getActeurNomComplet())
                .acteurRole(h.getActeurRole())
                .action(h.getAction())
                .titre(h.getTitre())
                .description(h.getDescription())
                .dateAction(h.getDateAction())
                .build();
    }
}