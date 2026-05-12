package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicateurResponse {

    private Long encadrementId;

    private Integer avancementActuel;
    private Integer nombreSuivis;

    private Integer nombreLivrables;
    private Integer livrablesValides;
    private Integer livrablesEnRetard;

    private Double moyenneLivrables;

    private String niveauRisque;
    private String statutProjet;
}