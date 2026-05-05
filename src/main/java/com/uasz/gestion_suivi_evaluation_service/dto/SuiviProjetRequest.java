package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class SuiviProjetRequest {

    @NotNull(message = "L'encadrement est obligatoire")
    private Long encadrementId;

    @NotNull(message = "L'avancement est obligatoire")
    @Min(0)
    @Max(100)
    private Integer avancementPourcentage;

    @NotNull(message = "La qualité du travail est obligatoire")
    @Min(0)
    @Max(20)
    private Integer qualiteTravail;

    @NotNull(message = "Le respect des délais est obligatoire")
    @Min(0)
    @Max(20)
    private Integer respectDelais;

    @NotNull(message = "La participation de l'étudiant est obligatoire")
    @Min(0)
    @Max(20)
    private Integer participationEtudiant;

    private String observations;
    private String recommandations;
}
