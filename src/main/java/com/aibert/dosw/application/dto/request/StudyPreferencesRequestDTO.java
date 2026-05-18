package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class StudyPreferencesRequestDTO {

    @NotNull(message = "El horario preferido de estudio es obligatorio")
    private StudyTime preferredStudyTime;

    @NotNull(message = "El método de estudio preferido es obligatorio")
    private StudyMethod preferredStudyMethod;

    @NotNull(message = "La meta de horas semanales de estudio es obligatoria")
    @Min(value = 1, message = "La meta de horas semanales debe ser al menos 1")
    @Max(value = 168, message = "La meta de horas semanales no puede superar 168")
    private Integer weeklyStudyHoursGoal;

    @NotBlank(message = "El lugar de estudio preferido es obligatorio")
    @Size(max = 100, message = "El lugar de estudio no puede superar 100 caracteres")
    private String preferredStudyLocation;

    private boolean notificationsEnabled;
}
