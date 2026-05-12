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
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long encadrementId;

    private Long acteurId;

    private String acteurNomComplet;

    private String acteurRole;

    private String action;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime dateAction;
}