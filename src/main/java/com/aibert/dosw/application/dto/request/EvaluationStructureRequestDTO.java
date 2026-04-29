package com.aibert.dosw.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationStructureRequestDTO {

    @NotNull(message = "Los cortes evaluativos son obligatorios")
    @NotEmpty(message = "La materia debe tener al menos un corte evaluativo")
    @Valid
    private List<EvaluationCutDTO> evaluationCuts;
}
