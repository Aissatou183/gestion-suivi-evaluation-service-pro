package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviRequest {

    @NotNull
    private Long encadrementId;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer avancementPourcentage;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer qualiteTravail;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer respectDelais;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer participationEtudiant;

    @NotBlank
    private String observations;

    private String recommandations;
}