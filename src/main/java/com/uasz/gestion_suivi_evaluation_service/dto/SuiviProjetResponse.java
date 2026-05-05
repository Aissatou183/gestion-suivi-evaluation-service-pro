package com.uasz.gestion_suivi_evaluation_service.dto;

import com.uasz.gestion_suivi_evaluation_service.entity.NiveauRisque;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviProjetResponse {
    private Long id;
    private Long encadrementId;
    private Long sujetId;
    private String sujetTitre;
    private Long etudiantId;
    private String etudiantNomComplet;
    private Long enseignantId;
    private String enseignantNomComplet;
    private Integer avancementPourcentage;
    private Integer qualiteTravail;
    private Integer respectDelais;
    private Integer participationEtudiant;
    private NiveauRisque niveauRisque;
    private String observations;
    private String recommandations;
    private LocalDateTime dateSuivi;
}
