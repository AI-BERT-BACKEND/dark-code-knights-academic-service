package com.aibert.dosw.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.aibert.dosw.application.dto.request.EvaluationCutDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationStructureRequestDTO {

    @NotNull(message = "La materia debe tener al menos un corte evaluativo")
    @NotEmpty(message = "La materia debe tener al menos un corte evaluativo")
    @Valid
    private List<EvaluationCutDTO> evaluationCuts;
}
