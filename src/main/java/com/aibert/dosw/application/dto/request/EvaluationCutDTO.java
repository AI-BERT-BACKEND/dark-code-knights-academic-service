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
@Schema(description = "A single evaluation cut with its weight percentage")
public class EvaluationCutDTO {

    @NotBlank(message = "El nombre del corte es obligatorio")
    @Size(max = 50, message = "El nombre del corte no puede superar 50 caracteres")
    @Schema(example = "Corte 1")
    private String cutName;

    @NotNull(message = "El porcentaje del corte es obligatorio")
    @DecimalMin(value = "1.0", message = "El porcentaje debe ser mínimo 1")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede superar 100")
    @Schema(example = "40.0", description = "Weight of this cut in the final grade (1.0–100.0). All cuts in a subject must sum to exactly 100.")
    private Double cutPercentage;
}
