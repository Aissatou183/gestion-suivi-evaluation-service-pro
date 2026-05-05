package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationProjetResponse {
    private Long id;
    private Long encadrementId;
    private Long sujetId;
    private String sujetTitre;
    private Long etudiantId;
    private String etudiantNomComplet;
    private Long enseignantId;
    private String enseignantNomComplet;
    private Integer noteGlobale;
    private String appreciation;
    private String pointsForts;
    private String pointsAAmeliorer;
    private LocalDateTime dateEvaluation;
}
