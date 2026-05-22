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
@Schema(description = "Request body for registering a grade activity inside an evaluation cut")
public class GradeRequestDTO {

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(max = 100, message = "El nombre de la actividad no puede superar 100 caracteres")
    @Schema(example = "Parcial 1")
    private String activityName;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    @Schema(example = "4.5", description = "Grade value on the Colombian 0.0–5.0 scale")
    private Double gradeValue;

    @NotNull(message = "El porcentaje de la actividad es obligatorio")
    @DecimalMin(value = "0.1", message = "El porcentaje debe ser mayor a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede superar 100")
    @Schema(example = "60.0", description = "Weight of this activity within the evaluation cut (0.1–100.0). Sum of all activities in the cut must not exceed 100.")
    private Double percentage;
}
