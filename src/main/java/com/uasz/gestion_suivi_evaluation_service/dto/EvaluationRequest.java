package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    @NotNull(message = "L'identifiant de l'encadrement est obligatoire")
    private Long encadrementId;

    @NotNull(message = "La note globale est obligatoire")
    @Min(value = 0, message = "La note minimale est 0")
    @Max(value = 20, message = "La note maximale est 20")
    private Integer noteGlobale;

    private String appreciation;

    private String pointsForts;

    private String pointsAAmeliorer;
}