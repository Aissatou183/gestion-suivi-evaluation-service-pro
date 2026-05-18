package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    private Long id;

    private Long encadrementId;

    /*
     * ENCADREUR
     */

    private Long enseignantId;

    private String enseignantNomComplet;

    /*
     * EVALUATION
     */

    private Integer noteGlobale;

    private String appreciation;

    private String pointsForts;

    private String pointsAAmeliorer;

    /*
     * DATE
     */

    private LocalDateTime dateEvaluation;
}