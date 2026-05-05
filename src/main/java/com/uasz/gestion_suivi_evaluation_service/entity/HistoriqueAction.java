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
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long encadrementId;
    private Long sujetId;

    @Column(nullable=false)
    private Long acteurId;

    @Column(nullable=false)
    private String acteurNomComplet;

    @Column(nullable=false)
    private String acteurRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=40)
    private TypeAction typeAction;

    @Column(nullable=false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime dateAction;
}
