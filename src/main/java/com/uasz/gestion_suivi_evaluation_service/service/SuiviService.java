package com.uasz.gestion_suivi_evaluation_service.service;

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

    public SuiviResponse creer(
            SuiviRequest request,
            Long enseignantId,
            String enseignantNomComplet,
            String role
    ) {
        String roleNettoye = role == null ? "" : role.replace("ROLE_", "");

        if (!"ENSEIGNANT".equals(roleNettoye)) {
            throw new RuntimeException("Seul un enseignant peut ajouter un suivi.");
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

    public List<SuiviResponse> parEncadrement(Long encadrementId) {
        return suiviProjetRepository.findByEncadrementIdOrderByDateSuiviDesc(encadrementId)
                .stream()
                .map(this::map)
                .toList();
    }

    private String calculerRisque(Integer avancement, Integer qualite, Integer delais, Integer participation) {
        double moyenne = (qualite + delais + participation) / 3.0;

        if (avancement < 30 || moyenne < 8) return "ELEVE";
        if (avancement < 60 || moyenne < 12) return "MOYEN";
        return "FAIBLE";
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