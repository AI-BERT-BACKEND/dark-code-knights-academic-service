package com.aibert.dosw.application.dto.request;

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
public class SimulationRequestDTO {

    @NotNull(message = "La nota objetivo no puede ser nula")
    @DecimalMin(value = "0.0", message = "La nota objetivo debe ser al menos 0.0")
    @DecimalMax(value = "5.0", message = "La nota objetivo no puede superar 5.0")
    private Double targetGrade;
}
