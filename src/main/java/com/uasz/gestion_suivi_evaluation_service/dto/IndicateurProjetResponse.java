package com.uasz.gestion_suivi_evaluation_service.dto;

import com.uasz.gestion_suivi_evaluation_service.entity.NiveauRisque;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicateurProjetResponse {
    private Long encadrementId;
    private Long sujetId;
    private String sujetTitre;
    private Long etudiantId;
    private String etudiantNomComplet;
    private Long enseignantId;
    private String enseignantNomComplet;

    private Integer avancementActuel;
    private Integer nombreSuivis;
    private Integer nombreLivrables;
    private Integer nombreLivrablesEvalues;
    private Double moyenneLivrables;
    private Integer derniereNoteProjet;
    private NiveauRisque niveauRisque;
    private String statutProjet;
}
