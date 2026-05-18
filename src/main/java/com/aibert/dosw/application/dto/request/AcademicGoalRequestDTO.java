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
public class AcademicGoalRequestDTO {

    @NotBlank(message = "El nombre de la meta es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de la meta debe tener entre 3 y 100 caracteres")
    private String goalName;

    @NotNull(message = "La nota objetivo es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota objetivo no puede ser menor a 0.0")
    @DecimalMax(value = "5.0", message = "La nota objetivo no puede ser mayor a 5.0")
    private Double targetGrade;

    private Long subjectId; // optional — null for general goals
}
