package com.uasz.gestion_suivi_evaluation_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncadrementResponse {

    private Long id;

    private Long sujetId;

    private String sujetTitre;

    private Long etudiantId;

    private String etudiantNomComplet;

    private Long enseignantId;

    private String enseignantNomComplet;

    private String statut;
}