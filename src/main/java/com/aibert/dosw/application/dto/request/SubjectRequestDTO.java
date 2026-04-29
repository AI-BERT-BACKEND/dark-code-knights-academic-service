package com.aibert.dosw.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDTO {

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String subjectName;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser mínimo 1")
    @Max(value = 10, message = "Los créditos deben ser máximo 10")
    private Integer credits;

    @NotBlank(message = "El nombre del docente es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre del docente debe tener entre 3 y 100 caracteres")
    private String teacherName;

    @NotBlank(message = "El semestre es obligatorio")
    private String semester;

    @NotNull(message = "Los cortes evaluativos son obligatorios")
    @NotEmpty(message = "La materia debe tener al menos un corte evaluativo")
    @Valid
    private List<EvaluationCutDTO> evaluationCuts;
}
