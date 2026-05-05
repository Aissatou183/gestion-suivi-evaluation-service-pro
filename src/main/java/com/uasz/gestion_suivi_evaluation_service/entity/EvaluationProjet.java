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
public class EvaluationProjet {

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
    private Integer noteGlobale;

    @Column(columnDefinition = "TEXT")
    private String appreciation;

    @Column(columnDefinition = "TEXT")
    private String pointsForts;

    @Column(columnDefinition = "TEXT")
    private String pointsAAmeliorer;

    private LocalDateTime dateEvaluation;
}
