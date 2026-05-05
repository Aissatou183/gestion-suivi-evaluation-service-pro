package com.uasz.gestion_suivi_evaluation_service.service;

import com.uasz.gestion_suivi_evaluation_service.client.*;
import com.uasz.gestion_suivi_evaluation_service.dto.*;
import com.uasz.gestion_suivi_evaluation_service.entity.*;
import com.uasz.gestion_suivi_evaluation_service.exception.BadRequestException;
import com.uasz.gestion_suivi_evaluation_service.repository.*;
import com.uasz.gestion_suivi_evaluation_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SuiviEvaluationService {

    private final SuiviProjetRepository suiviRepository;
    private final EvaluationProjetRepository evaluationRepository;
    private final HistoriqueActionRepository historiqueRepository;
    private final EncadrementClient encadrementClient;
    private final LivrableClient livrableClient;
    private final JwtService jwtService;

    public SuiviProjetResponse creerSuivi(SuiviProjetRequest request, String token) {
        Long enseignantId = jwtService.extractUserId(token);

        EncadrementResponse encadrement = encadrementClient.getEncadrement(request.getEncadrementId(), token);
        verifierEncadrementActif(encadrement);

        if (!enseignantId.equals(encadrement.getEnseignantId())) {
            throw new BadRequestException("Seul l'enseignant encadreur peut créer un suivi");
        }

        NiveauRisque risque = calculerRisque(request.getAvancementPourcentage(), request.getRespectDelais());

        SuiviProjet suivi = SuiviProjet.builder()
                .encadrementId(encadrement.getId())
                .sujetId(encadrement.getSujetId())
                .sujetTitre(encadrement.getSujetTitre())
                .etudiantId(encadrement.getEtudiantId())
                .etudiantNomComplet(encadrement.getEtudiantNomComplet())
                .enseignantId(encadrement.getEnseignantId())
                .enseignantNomComplet(encadrement.getEnseignantNomComplet())
                .avancementPourcentage(request.getAvancementPourcentage())
                .qualiteTravail(request.getQualiteTravail())
                .respectDelais(request.getRespectDelais())
                .participationEtudiant(request.getParticipationEtudiant())
                .niveauRisque(risque)
                .observations(request.getObservations())
                .recommandations(request.getRecommandations())
                .dateSuivi(LocalDateTime.now())
                .build();

        SuiviProjet saved = suiviRepository.save(suivi);

        enregistrerHistorique(
                encadrement.getId(),
                encadrement.getSujetId(),
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token),
                TypeAction.CREATION_SUIVI,
                "Nouveau suivi du projet",
                "Avancement : " + request.getAvancementPourcentage() + "%"
        );

        return toSuiviResponse(saved);
    }

    public EvaluationProjetResponse evaluerProjet(EvaluationProjetRequest request, String token) {
        Long enseignantId = jwtService.extractUserId(token);

        EncadrementResponse encadrement = encadrementClient.getEncadrement(request.getEncadrementId(), token);
        verifierEncadrementActif(encadrement);

        if (!enseignantId.equals(encadrement.getEnseignantId())) {
            throw new BadRequestException("Seul l'enseignant encadreur peut évaluer ce projet");
        }

        EvaluationProjet evaluation = EvaluationProjet.builder()
                .encadrementId(encadrement.getId())
                .sujetId(encadrement.getSujetId())
                .sujetTitre(encadrement.getSujetTitre())
                .etudiantId(encadrement.getEtudiantId())
                .etudiantNomComplet(encadrement.getEtudiantNomComplet())
                .enseignantId(encadrement.getEnseignantId())
                .enseignantNomComplet(encadrement.getEnseignantNomComplet())
                .noteGlobale(request.getNoteGlobale())
                .appreciation(request.getAppreciation())
                .pointsForts(request.getPointsForts())
                .pointsAAmeliorer(request.getPointsAAmeliorer())
                .dateEvaluation(LocalDateTime.now())
                .build();

        EvaluationProjet saved = evaluationRepository.save(evaluation);

        enregistrerHistorique(
                encadrement.getId(),
                encadrement.getSujetId(),
                jwtService.extractUserId(token),
                jwtService.extractNomComplet(token),
                jwtService.extractRole(token),
                TypeAction.EVALUATION_PROJET,
                "Évaluation du projet",
                "Note globale : " + request.getNoteGlobale() + "/20"
        );

        return toEvaluationResponse(saved);
    }

    public HistoriqueActionResponse creerAction(HistoriqueActionRequest request, String token) {
        HistoriqueAction action = HistoriqueAction.builder()
                .encadrementId(request.getEncadrementId())
                .sujetId(request.getSujetId())
                .acteurId(jwtService.extractUserId(token))
                .acteurNomComplet(jwtService.extractNomComplet(token))
                .acteurRole(jwtService.extractRole(token))
                .typeAction(request.getTypeAction())
                .titre(request.getTitre())
                .description(request.getDescription())
                .dateAction(LocalDateTime.now())
                .build();

        return toHistoriqueResponse(historiqueRepository.save(action));
    }

    public IndicateurProjetResponse indicateurs(Long encadrementId, String token) {
        EncadrementResponse encadrement = encadrementClient.getEncadrement(encadrementId, token);
        verifierEncadrement(encadrement);

        Optional<SuiviProjet> dernierSuivi = suiviRepository.findTopByEncadrementIdOrderByDateSuiviDesc(encadrementId);
        Optional<EvaluationProjet> derniereEvaluation = evaluationRepository.findTopByEncadrementIdOrderByDateEvaluationDesc(encadrementId);
        List<LivrableResponse> livrables = livrableClient.getLivrablesParEncadrement(encadrementId, token);

        int nombreLivrables = livrables.size();
        int nombreLivrablesEvalues = (int) livrables.stream().filter(l -> l.getNote() != null).count();

        Double moyenneLivrables = livrables.stream()
                .filter(l -> l.getNote() != null)
                .mapToInt(LivrableResponse::getNote)
                .average()
                .orElse(0.0);

        Integer avancement = dernierSuivi.map(SuiviProjet::getAvancementPourcentage).orElse(0);
        NiveauRisque risque = dernierSuivi.map(SuiviProjet::getNiveauRisque).orElse(NiveauRisque.MOYEN);
        Integer noteProjet = derniereEvaluation.map(EvaluationProjet::getNoteGlobale).orElse(null);

        return IndicateurProjetResponse.builder()
                .encadrementId(encadrement.getId())
                .sujetId(encadrement.getSujetId())
                .sujetTitre(encadrement.getSujetTitre())
                .etudiantId(encadrement.getEtudiantId())
                .etudiantNomComplet(encadrement.getEtudiantNomComplet())
                .enseignantId(encadrement.getEnseignantId())
                .enseignantNomComplet(encadrement.getEnseignantNomComplet())
                .avancementActuel(avancement)
                .nombreSuivis((int) suiviRepository.countByEncadrementId(encadrementId))
                .nombreLivrables(nombreLivrables)
                .nombreLivrablesEvalues(nombreLivrablesEvalues)
                .moyenneLivrables(Math.round(moyenneLivrables * 100.0) / 100.0)
                .derniereNoteProjet(noteProjet)
                .niveauRisque(risque)
                .statutProjet(calculerStatutProjet(avancement, risque))
                .build();
    }

    public List<SuiviProjetResponse> suivisParEncadrement(Long encadrementId) {
        return suiviRepository.findByEncadrementIdOrderByDateSuiviDesc(encadrementId)
                .stream().map(this::toSuiviResponse).toList();
    }

    public List<HistoriqueActionResponse> historiqueParEncadrement(Long encadrementId) {
        return historiqueRepository.findByEncadrementIdOrderByDateActionDesc(encadrementId)
                .stream().map(this::toHistoriqueResponse).toList();
    }

    public List<HistoriqueActionResponse> historiqueTous() {
        return historiqueRepository.findAllByOrderByDateActionDesc()
                .stream().map(this::toHistoriqueResponse).toList();
    }

    public List<EvaluationProjetResponse> evaluationsParEncadrement(Long encadrementId) {
        return evaluationRepository.findByEncadrementIdOrderByDateEvaluationDesc(encadrementId)
                .stream().map(this::toEvaluationResponse).toList();
    }

    private void verifierEncadrement(EncadrementResponse encadrement) {
        if (encadrement == null) {
            throw new BadRequestException("Encadrement introuvable");
        }
    }

    private void verifierEncadrementActif(EncadrementResponse encadrement) {
        verifierEncadrement(encadrement);
        if (!"ACTIF".equals(encadrement.getStatut())) {
            throw new BadRequestException("Le suivi ou l'évaluation nécessite un encadrement actif");
        }
    }

    private NiveauRisque calculerRisque(Integer avancement, Integer respectDelais) {
        if (avancement < 30 || respectDelais < 8) return NiveauRisque.ELEVE;
        if (avancement < 60 || respectDelais < 12) return NiveauRisque.MOYEN;
        return NiveauRisque.FAIBLE;
    }

    private String calculerStatutProjet(Integer avancement, NiveauRisque risque) {
        if (avancement >= 100) return "TERMINE";
        if (risque == NiveauRisque.ELEVE) return "EN_RISQUE";
        if (avancement >= 70) return "BIEN_AVANCE";
        if (avancement >= 30) return "EN_COURS";
        return "DEMARRAGE";
    }

    private void enregistrerHistorique(Long encadrementId, Long sujetId, Long acteurId, String acteurNom, String role,
                                       TypeAction typeAction, String titre, String description) {
        HistoriqueAction action = HistoriqueAction.builder()
                .encadrementId(encadrementId)
                .sujetId(sujetId)
                .acteurId(acteurId)
                .acteurNomComplet(acteurNom)
                .acteurRole(role)
                .typeAction(typeAction)
                .titre(titre)
                .description(description)
                .dateAction(LocalDateTime.now())
                .build();

        historiqueRepository.save(action);
    }

    private SuiviProjetResponse toSuiviResponse(SuiviProjet s) {
        return SuiviProjetResponse.builder()
                .id(s.getId())
                .encadrementId(s.getEncadrementId())
                .sujetId(s.getSujetId())
                .sujetTitre(s.getSujetTitre())
                .etudiantId(s.getEtudiantId())
                .etudiantNomComplet(s.getEtudiantNomComplet())
                .enseignantId(s.getEnseignantId())
                .enseignantNomComplet(s.getEnseignantNomComplet())
                .avancementPourcentage(s.getAvancementPourcentage())
                .qualiteTravail(s.getQualiteTravail())
                .respectDelais(s.getRespectDelais())
                .participationEtudiant(s.getParticipationEtudiant())
                .niveauRisque(s.getNiveauRisque())
                .observations(s.getObservations())
                .recommandations(s.getRecommandations())
                .dateSuivi(s.getDateSuivi())
                .build();
    }

    private EvaluationProjetResponse toEvaluationResponse(EvaluationProjet e) {
        return EvaluationProjetResponse.builder()
                .id(e.getId())
                .encadrementId(e.getEncadrementId())
                .sujetId(e.getSujetId())
                .sujetTitre(e.getSujetTitre())
                .etudiantId(e.getEtudiantId())
                .etudiantNomComplet(e.getEtudiantNomComplet())
                .enseignantId(e.getEnseignantId())
                .enseignantNomComplet(e.getEnseignantNomComplet())
                .noteGlobale(e.getNoteGlobale())
                .appreciation(e.getAppreciation())
                .pointsForts(e.getPointsForts())
                .pointsAAmeliorer(e.getPointsAAmeliorer())
                .dateEvaluation(e.getDateEvaluation())
                .build();
    }

    private HistoriqueActionResponse toHistoriqueResponse(HistoriqueAction h) {
        return HistoriqueActionResponse.builder()
                .id(h.getId())
                .encadrementId(h.getEncadrementId())
                .sujetId(h.getSujetId())
                .acteurId(h.getActeurId())
                .acteurNomComplet(h.getActeurNomComplet())
                .acteurRole(h.getActeurRole())
                .typeAction(h.getTypeAction())
                .titre(h.getTitre())
                .description(h.getDescription())
                .dateAction(h.getDateAction())
                .build();
    }
}
