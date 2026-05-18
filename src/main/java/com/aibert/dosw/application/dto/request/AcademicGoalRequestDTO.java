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
public class AcademicGoalRequestDTO {

    @NotNull(message = "La nota objetivo es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota objetivo no puede ser menor a 0.0")
    @DecimalMax(value = "5.0", message = "La nota objetivo no puede ser mayor a 5.0")
    private Double targetGrade;
}
