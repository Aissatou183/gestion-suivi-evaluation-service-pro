package com.uasz.gestion_suivi_evaluation_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SuiviProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long encadrementId;

    @Column(nullable=false)
    private Long sujetId;

    @Column(nullable=false)
    private String sujetTitre;

    @Column(nullable=false)
    private Long etudiantId;

    @Column(nullable=false)
    private String etudiantNomComplet;

    @Column(nullable=false)
    private Long enseignantId;

    @Column(nullable=false)
    private String enseignantNomComplet;

    @Column(nullable=false)
    private Integer avancementPourcentage;

    @Column(nullable=false)
    private Integer qualiteTravail;

    @Column(nullable=false)
    private Integer respectDelais;

    @Column(nullable=false)
    private Integer participationEtudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private NiveauRisque niveauRisque;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    private LocalDateTime dateSuivi;
}
