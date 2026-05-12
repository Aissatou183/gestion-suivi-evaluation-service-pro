package com.uasz.gestion_suivi_evaluation_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    @NotNull
    private Long encadrementId;

    @NotNull
    @Min(0)
    @Max(20)
    private Integer noteGlobale;

    private String appreciation;
    private String pointsForts;
    private String pointsAAmeliorer;
}