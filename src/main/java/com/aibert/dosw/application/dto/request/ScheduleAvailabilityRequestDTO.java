package com.aibert.dosw.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAvailabilityRequestDTO {

    @Positive(message = "Las horas libres deben ser mayores a 0")
    private Double freeTimeHours;

    @Positive(message = "Las horas de descanso deben ser mayores a 0")
    private Double restHours;

    @Positive(message = "Las horas de tiempo personal deben ser mayores a 0")
    private Double personalTimeHours;

    @Positive(message = "Las horas de tiempo social deben ser mayores a 0")
    private Double socialTimeHours;

    @Positive(message = "Las horas máximas de estudio por día deben ser mayores a 0")
    @DecimalMax(value = "15.0", message = "Las horas máximas de estudio por día no pueden superar 15")
    private Double maxStudyHoursPerDay;
}
