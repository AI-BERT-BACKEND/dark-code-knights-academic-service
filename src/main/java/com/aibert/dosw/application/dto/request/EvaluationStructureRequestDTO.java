package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for replacing the evaluation cut structure of a subject")
public class EvaluationStructureRequestDTO {

    @NotNull(message = "La materia debe tener al menos un corte evaluativo")
    @NotEmpty(message = "La materia debe tener al menos un corte evaluativo")
    @Valid
    @Schema(description = "Complete list of evaluation cuts. All cutPercentage values must sum to exactly 100.")
    private List<EvaluationCutDTO> evaluationCuts;
}
