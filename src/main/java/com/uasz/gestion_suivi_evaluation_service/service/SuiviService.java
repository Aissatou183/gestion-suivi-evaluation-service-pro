package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.EncadrementClient;
import com.uasz.gestion_suivi_evaluation_service.dto.*;
import com.uasz.gestion_suivi_evaluation_service.entity.SuiviProjet;
import com.uasz.gestion_suivi_evaluation_service.repository.SuiviProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuiviService {

    private final SuiviProjetRepository suiviProjetRepository;
    private final HistoriqueService historiqueService;
    private final EncadrementClient encadrementClient;

    public SuiviResponse creer(
            SuiviRequest request,
            Long enseignantId,
            String enseignantNomComplet,
            String role,
            String token
    ) {
        String roleNettoye = normaliserRole(role);

        if (!"ENSEIGNANT".equals(roleNettoye)) {
            throw new RuntimeException("Seul un enseignant peut ajouter un suivi.");
        }

        EncadrementResponse encadrement =
                encadrementClient.trouverParId(request.getEncadrementId(), token);

        if (encadrement == null) {
            throw new RuntimeException("Encadrement introuvable.");
        }

        if (!"ACTIF".equalsIgnoreCase(String.valueOf(encadrement.getStatut()))) {
            throw new RuntimeException("Impossible d'ajouter un suivi sur un encadrement non actif.");
        }

        if (encadrement.getEnseignantId() == null ||
                !encadrement.getEnseignantId().equals(enseignantId)) {
            throw new RuntimeException("Vous n'êtes pas encadreur actif de ce projet.");
        }

        String risque = calculerRisque(
                request.getAvancementPourcentage(),
                request.getQualiteTravail(),
                request.getRespectDelais(),
                request.getParticipationEtudiant()
        );

        SuiviProjet suivi = SuiviProjet.builder()
                .encadrementId(request.getEncadrementId())
                .enseignantId(enseignantId)
                .enseignantNomComplet(enseignantNomComplet)
                .avancementPourcentage(request.getAvancementPourcentage())
                .qualiteTravail(request.getQualiteTravail())
                .respectDelais(request.getRespectDelais())
                .participationEtudiant(request.getParticipationEtudiant())
                .observations(request.getObservations())
                .recommandations(request.getRecommandations())
                .niveauRisque(risque)
                .dateSuivi(LocalDateTime.now())
                .build();

        suivi = suiviProjetRepository.save(suivi);

        historiqueService.ajouter(
                request.getEncadrementId(),
                enseignantId,
                enseignantNomComplet,
                roleNettoye,
                "AJOUT_SUIVI",
                "Nouveau suivi ajouté",
                "Avancement : " + request.getAvancementPourcentage() + "%, risque : " + risque
        );

        return map(suivi);
    }

    public List<SuiviResponse> parEncadrement(
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
            return lister(encadrementId);
        }

        if ("ENSEIGNANT".equals(roleNettoye)) {
            if (encadrement.getEnseignantId() != null &&
                    encadrement.getEnseignantId().equals(userId)) {
                return lister(encadrementId);
            }

            throw new RuntimeException("Accès refusé : vous n'êtes pas encadreur de ce projet.");
        }

        if ("ETUDIANT".equals(roleNettoye)) {
            if (encadrement.getEtudiantId() != null &&
                    encadrement.getEtudiantId().equals(userId)) {
                return lister(encadrementId);
            }

            throw new RuntimeException("Accès refusé : ce projet ne vous appartient pas.");
        }

        throw new RuntimeException("Rôle non autorisé.");
    }

    private List<SuiviResponse> lister(Long encadrementId) {
        return suiviProjetRepository
                .findByEncadrementIdOrderByDateSuiviDesc(encadrementId)
                .stream()
                .map(this::map)
                .toList();
    }

    private String calculerRisque(
            Integer avancement,
            Integer qualite,
            Integer delais,
            Integer participation
    ) {
        int avancementSafe = avancement == null ? 0 : avancement;
        int qualiteSafe = qualite == null ? 0 : qualite;
        int delaisSafe = delais == null ? 0 : delais;
        int participationSafe = participation == null ? 0 : participation;

        double moyenne = (qualiteSafe + delaisSafe + participationSafe) / 3.0;

        if (avancementSafe < 30 || moyenne < 8) {
            return "ELEVE";
        }

        if (avancementSafe < 60 || moyenne < 12) {
            return "MOYEN";
        }

        return "FAIBLE";
    }

    private String normaliserRole(String role) {
        if (role == null) {
            return "";
        }

        return role.replace("ROLE_", "").trim().toUpperCase();
    }

    private SuiviResponse map(SuiviProjet s) {
        return SuiviResponse.builder()
                .id(s.getId())
                .encadrementId(s.getEncadrementId())
                .enseignantId(s.getEnseignantId())
                .enseignantNomComplet(s.getEnseignantNomComplet())
                .avancementPourcentage(s.getAvancementPourcentage())
                .qualiteTravail(s.getQualiteTravail())
                .respectDelais(s.getRespectDelais())
                .participationEtudiant(s.getParticipationEtudiant())
                .observations(s.getObservations())
                .recommandations(s.getRecommandations())
                .niveauRisque(s.getNiveauRisque())
                .dateSuivi(s.getDateSuivi())
                .build();
    }
}