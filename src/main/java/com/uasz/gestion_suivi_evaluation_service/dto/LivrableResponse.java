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

    private Long sujetId;
    private String sujetTitre;

    private Long etudiantId;
    private String etudiantNomComplet;
    private String etudiantNiveau;

    private Long enseignantId;
    private String enseignantNomComplet;

    // PRINCIPAL ou SECONDAIRE
    private String typeEncadreur;

    private String typeLivrable;

    private Integer version;

    // DEPOSE / EN_RETARD / EVALUE / VALIDE
    private String statut;

    private Integer note;

    private String commentaireEvaluation;

    private LocalDateTime dateDepot;

    private LocalDateTime dateEvaluation;
}