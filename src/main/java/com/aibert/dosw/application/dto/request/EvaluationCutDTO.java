package com.aibert.dosw.application.dto.request;

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
public class EvaluationCutDTO {

    @NotBlank(message = "El nombre del corte es obligatorio")
    @Size(max = 50, message = "El nombre del corte no puede superar 50 caracteres")
    private String cutName;

    @NotNull(message = "El porcentaje del corte es obligatorio")
    @DecimalMin(value = "1.0", message = "El porcentaje debe ser mínimo 1")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede superar 100")
    private Double cutPercentage;
}
