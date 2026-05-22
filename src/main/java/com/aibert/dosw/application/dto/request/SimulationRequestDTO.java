package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for simulating the grade needed to reach a target final grade")
public class SimulationRequestDTO {

    @NotNull(message = "La nota objetivo no puede ser nula")
    @DecimalMin(value = "0.0", message = "La nota objetivo debe ser al menos 0.0")
    @DecimalMax(value = "5.0", message = "La nota objetivo no puede superar 5.0")
    @Schema(example = "4.0", description = "Target final grade the student wants to achieve (0.0–5.0 scale)")
    private Double targetGrade;
}
