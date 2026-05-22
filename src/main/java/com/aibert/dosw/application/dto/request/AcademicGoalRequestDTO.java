package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating an academic goal (upsert by goalName)")
public class AcademicGoalRequestDTO {

    @NotBlank(message = "El nombre de la meta es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de la meta debe tener entre 3 y 100 caracteres")
    @Schema(example = "Aprobar Cálculo Diferencial")
    private String goalName;

    @NotNull(message = "La nota objetivo es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota objetivo no puede ser menor a 0.0")
    @DecimalMax(value = "5.0", message = "La nota objetivo no puede ser mayor a 5.0")
    @Schema(example = "3.5", description = "Target grade the student wants to achieve (0.0–5.0 scale)")
    private Double targetGrade;

    @Schema(example = "1", description = "Numeric ID of the linked subject. Omit or set to null for a general goal not tied to a specific subject.")
    private Long subjectId; // optional — null for general goals
}
