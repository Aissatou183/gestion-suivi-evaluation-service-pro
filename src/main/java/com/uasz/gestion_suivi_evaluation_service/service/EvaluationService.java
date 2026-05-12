package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.dto.*;
import com.uasz.gestion_suivi_evaluation_service.entity.EvaluationProjet;
import com.uasz.gestion_suivi_evaluation_service.repository.EvaluationProjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationProjetRepository evaluationRepository;
    private final HistoriqueService historiqueService;

    public EvaluationResponse creer(
            EvaluationRequest request,
            Long enseignantId,
            String enseignantNomComplet,
            String role
    ) {
        if (!"ENSEIGNANT".equals(role)) {
            throw new RuntimeException("Seul un enseignant peut évaluer le projet.");
        }

        EvaluationProjet evaluation = EvaluationProjet.builder()
                .encadrementId(request.getEncadrementId())
                .enseignantId(enseignantId)
                .enseignantNomComplet(enseignantNomComplet)
                .noteGlobale(request.getNoteGlobale())
                .appreciation(request.getAppreciation())
                .pointsForts(request.getPointsForts())
                .pointsAAmeliorer(request.getPointsAAmeliorer())
                .dateEvaluation(LocalDateTime.now())
                .build();

        evaluation = evaluationRepository.save(evaluation);

        historiqueService.ajouter(
                request.getEncadrementId(),
                enseignantId,
                enseignantNomComplet,
                "ENSEIGNANT",
                "EVALUATION_PROJET",
                "Évaluation globale du projet",
                "Note globale : " + request.getNoteGlobale() + "/20"
        );

        return map(evaluation);
    }

    public List<EvaluationResponse> parEncadrement(Long encadrementId) {
        return evaluationRepository.findByEncadrementIdOrderByDateEvaluationDesc(encadrementId)
                .stream()
                .map(this::map)
                .toList();
    }

    private EvaluationResponse map(EvaluationProjet e) {
        return EvaluationResponse.builder()
                .id(e.getId())
                .encadrementId(e.getEncadrementId())
                .enseignantId(e.getEnseignantId())
                .enseignantNomComplet(e.getEnseignantNomComplet())
                .noteGlobale(e.getNoteGlobale())
                .appreciation(e.getAppreciation())
                .pointsForts(e.getPointsForts())
                .pointsAAmeliorer(e.getPointsAAmeliorer())
                .dateEvaluation(e.getDateEvaluation())
                .build();
    }
}