package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicateurResponse {

    private Long encadrementId;

    /*
     * AVANCEMENT
     */

    private Integer avancementActuel;

    /*
     * SUIVIS
     */

    private Integer nombreSuivis;

    /*
     * LIVRABLES
     */

    private Integer nombreLivrables;

    private Integer livrablesValides;

    private Integer livrablesEnRetard;

    /*
     * MOYENNE
     */

    private Double moyenneLivrables;

    /*
     * RISQUE & STATUT
     */

    private String niveauRisque;

    private String statutProjet;
}