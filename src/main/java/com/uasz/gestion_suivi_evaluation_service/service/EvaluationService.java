package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.EncadrementClient;
import com.uasz.gestion_suivi_evaluation_service.dto.EncadrementResponse;
import com.uasz.gestion_suivi_evaluation_service.dto.EvaluationRequest;
import com.uasz.gestion_suivi_evaluation_service.dto.EvaluationResponse;
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
    private final EncadrementClient encadrementClient;

    public EvaluationResponse creer(
            EvaluationRequest request,
            Long enseignantId,
            String enseignantNomComplet,
            String role,
            String token
    ) {

        verifierRequest(request);

        String roleNettoye = normaliserRole(role);

        if (!"ENSEIGNANT".equals(roleNettoye)) {
            throw new RuntimeException(
                    "Seul un enseignant peut évaluer le projet."
            );
        }

        EncadrementResponse encadrement =
                verifierAccesEncadreur(
                        request.getEncadrementId(),
                        enseignantId,
                        token
                );

        EvaluationProjet evaluation = EvaluationProjet.builder()
                .encadrementId(request.getEncadrementId())
                .enseignantId(enseignantId)
                .enseignantNomComplet(
                        nettoyer(enseignantNomComplet)
                )
                .noteGlobale(request.getNoteGlobale())
                .appreciation(
                        nettoyer(request.getAppreciation())
                )
                .pointsForts(
                        nettoyer(request.getPointsForts())
                )
                .pointsAAmeliorer(
                        nettoyer(request.getPointsAAmeliorer())
                )
                .dateEvaluation(LocalDateTime.now())
                .build();

        evaluation = evaluationRepository.save(evaluation);

        historiqueService.ajouter(
                request.getEncadrementId(),
                enseignantId,
                enseignantNomComplet,
                roleNettoye,
                "EVALUATION_PROJET",
                "Évaluation globale du projet",
                "Note globale attribuée : "
                        + request.getNoteGlobale()
                        + "/20"
        );

        return map(evaluation);
    }

    public List<EvaluationResponse> parEncadrement(
            Long encadrementId,
            Long userId,
            String role,
            String token
    ) {

        verifierAccesLecture(
                encadrementId,
                userId,
                role,
                token
        );

        return lister(encadrementId);
    }

    /*
     * VALIDATIONS
     */

    private void verifierRequest(EvaluationRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "La requête d'évaluation est obligatoire."
            );
        }

        if (request.getEncadrementId() == null) {
            throw new RuntimeException(
                    "L'encadrement est obligatoire."
            );
        }

        if (request.getNoteGlobale() == null) {
            throw new RuntimeException(
                    "La note globale est obligatoire."
            );
        }

        if (request.getNoteGlobale() < 0
                || request.getNoteGlobale() > 20) {

            throw new RuntimeException(
                    "La note globale doit être comprise entre 0 et 20."
            );
        }
    }

    private EncadrementResponse verifierAccesEncadreur(
            Long encadrementId,
            Long enseignantId,
            String token
    ) {

        EncadrementResponse encadrement =
                encadrementClient.trouverParId(
                        encadrementId,
                        token
                );

        if (encadrement == null) {
            throw new RuntimeException(
                    "Encadrement introuvable."
            );
        }

        if (!"ACTIF".equalsIgnoreCase(
                String.valueOf(encadrement.getStatut()))
        ) {

            throw new RuntimeException(
                    "Impossible d'évaluer un encadrement non actif."
            );
        }

        if (encadrement.getEnseignantId() == null
                || !encadrement.getEnseignantId().equals(enseignantId)) {

            throw new RuntimeException(
                    "Vous n'êtes pas encadreur actif de ce projet."
            );
        }

        return encadrement;
    }

    private void verifierAccesLecture(
            Long encadrementId,
            Long userId,
            String role,
            String token
    ) {

        String roleNettoye = normaliserRole(role);

        EncadrementResponse encadrement =
                encadrementClient.trouverParId(
                        encadrementId,
                        token
                );

        if (encadrement == null) {
            throw new RuntimeException(
                    "Encadrement introuvable."
            );
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

        throw new RuntimeException(
                "Rôle non autorisé."
        );
    }

    /*
     * HELPERS
     */

    private List<EvaluationResponse> lister(Long encadrementId) {

        return evaluationRepository
                .findByEncadrementIdOrderByDateEvaluationDesc(encadrementId)
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