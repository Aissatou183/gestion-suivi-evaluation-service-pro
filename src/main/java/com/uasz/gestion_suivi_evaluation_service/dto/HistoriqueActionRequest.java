package com.uasz.gestion_suivi_evaluation_service.dto;

import com.uasz.gestion_suivi_evaluation_service.entity.TypeAction;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class HistoriqueActionRequest {

    private Long encadrementId;
    private Long sujetId;

    @NotNull(message = "Le type d'action est obligatoire")
    private TypeAction typeAction;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;
}
