package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating or updating an academic subject")
public class SubjectRequestDTO {

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(example = "Álgebra Lineal")
    private String subjectName;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser mínimo 1")
    @Max(value = 4, message = "Los créditos deben ser máximo 4")
    @Schema(example = "3")
    private Integer credits;

    @NotBlank(message = "El nombre del docente es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre del docente debe tener entre 3 y 100 caracteres")
    @Schema(example = "Prof. Martínez")
    private String teacherName;

    @NotBlank(message = "El semestre es obligatorio")
    @Pattern(
            regexp = "^\\d{4}-[12]$",
            message = "El semestre debe tener el formato YYYY-1 o YYYY-2 (ejemplo: 2025-1)"
    )
    @Schema(example = "2025-1")
    private String semester;

    @NotBlank(message = "El horario es obligatorio")
    @Size(min = 3, max = 100, message = "El horario debe tener entre 3 y 100 caracteres")
    @Schema(example = "LUNES 08:00-10:00")
    private String schedule;

    @NotEmpty(message = "La materia debe tener al menos un corte evaluativo")
    @Valid
    @Schema(description = "List of evaluation cuts whose percentages must sum to exactly 100")
    private List<EvaluationCutDTO> evaluationCuts;
}
