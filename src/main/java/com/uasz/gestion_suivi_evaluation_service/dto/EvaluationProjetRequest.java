package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class EvaluationProjetRequest {

    @NotNull(message = "L'encadrement est obligatoire")
    private Long encadrementId;

    @NotNull(message = "La note globale est obligatoire")
    @Min(0)
    @Max(20)
    private Integer noteGlobale;

    @NotBlank(message = "L'appréciation est obligatoire")
    private String appreciation;

    private String pointsForts;
    private String pointsAAmeliorer;
}
