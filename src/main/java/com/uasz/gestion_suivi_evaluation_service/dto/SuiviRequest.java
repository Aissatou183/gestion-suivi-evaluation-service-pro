package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuiviRequest {

    @NotNull(message = "L'identifiant de l'encadrement est obligatoire")
    private Long encadrementId;

    @NotNull(message = "Le pourcentage d'avancement est obligatoire")
    @Min(value = 0, message = "L'avancement minimum est 0%")
    @Max(value = 100, message = "L'avancement maximum est 100%")
    private Integer avancementPourcentage;

    @NotNull(message = "La qualité du travail est obligatoire")
    @Min(value = 0, message = "La qualité minimale est 0")
    @Max(value = 20, message = "La qualité maximale est 20")
    private Integer qualiteTravail;

    @NotNull(message = "Le respect des délais est obligatoire")
    @Min(value = 0, message = "Le respect des délais minimum est 0")
    @Max(value = 20, message = "Le respect des délais maximum est 20")
    private Integer respectDelais;

    @NotNull(message = "La participation de l'étudiant est obligatoire")
    @Min(value = 0, message = "La participation minimale est 0")
    @Max(value = 20, message = "La participation maximale est 20")
    private Integer participationEtudiant;

    @NotBlank(message = "Les observations sont obligatoires")
    private String observations;

    private String recommandations;
}