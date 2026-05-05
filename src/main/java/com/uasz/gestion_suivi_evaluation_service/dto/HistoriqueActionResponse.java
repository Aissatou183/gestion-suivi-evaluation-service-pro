package com.uasz.gestion_suivi_evaluation_service.dto;

import com.uasz.gestion_suivi_evaluation_service.entity.TypeAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueActionResponse {
    private Long id;
    private Long encadrementId;
    private Long sujetId;
    private Long acteurId;
    private String acteurNomComplet;
    private String acteurRole;
    private TypeAction typeAction;
    private String titre;
    private String description;
    private LocalDateTime dateAction;
}
