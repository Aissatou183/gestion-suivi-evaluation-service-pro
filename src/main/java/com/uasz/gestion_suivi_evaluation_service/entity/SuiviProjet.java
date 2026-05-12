package com.uasz.gestion_suivi_evaluation_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long encadrementId;

    private Long enseignantId;
    private String enseignantNomComplet;

    @Column(nullable = false)
    private Integer avancementPourcentage;

    @Column(nullable = false)
    private Integer qualiteTravail;

    @Column(nullable = false)
    private Integer respectDelais;

    @Column(nullable = false)
    private Integer participationEtudiant;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    private String niveauRisque;

    @Column(nullable = false)
    private LocalDateTime dateSuivi;
}