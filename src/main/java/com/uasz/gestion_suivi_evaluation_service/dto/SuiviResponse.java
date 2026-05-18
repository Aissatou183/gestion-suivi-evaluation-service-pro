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

    /*
     * ENCADREMENT
     */

    private Long encadrementId;

    /*
     * ENSEIGNANT
     */

    private Long enseignantId;

    private String enseignantNomComplet;

    /*
     * SUIVI
     */

    private Integer avancementPourcentage;

    private Integer qualiteTravail;

    private Integer respectDelais;

    private Integer participationEtudiant;

    private String observations;

    private String recommandations;

    /*
     * RISQUE
     */

    private String niveauRisque;

    /*
     * DATE
     */

    private LocalDateTime dateSuivi;
}