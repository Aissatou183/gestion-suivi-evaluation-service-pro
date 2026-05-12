package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivrableResponse {

    private Long id;
    private Long encadrementId;

    private String typeLivrable;
    private Integer version;

    private String statut;
    private Integer note;

    private String commentaireEvaluation;

    private LocalDateTime dateDepot;
    private LocalDateTime dateEvaluation;
}