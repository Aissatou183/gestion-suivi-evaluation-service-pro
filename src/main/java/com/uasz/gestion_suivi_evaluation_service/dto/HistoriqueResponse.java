package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueResponse {

    private Long id;

    private Long encadrementId;

    /*
     * ACTEUR
     */

    private Long acteurId;

    private String acteurNomComplet;

    private String acteurRole;

    /*
     * ACTION
     */

    private String action;

    private String titre;

    private String description;

    /*
     * DATE
     */

    private LocalDateTime dateAction;
}