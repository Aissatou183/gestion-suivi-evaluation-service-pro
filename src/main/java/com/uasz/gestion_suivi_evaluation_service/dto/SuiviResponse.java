package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviResponse {

    private Long id;
    private Long encadrementId;

    private Long enseignantId;
    private String enseignantNomComplet;

    private Integer avancementPourcentage;
    private Integer qualiteTravail;
    private Integer respectDelais;
    private Integer participationEtudiant;

    private String observations;
    private String recommandations;
    private String niveauRisque;

    private LocalDateTime dateSuivi;
}