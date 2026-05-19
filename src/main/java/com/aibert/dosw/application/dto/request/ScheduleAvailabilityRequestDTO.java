package com.aibert.dosw.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAvailabilityRequestDTO {

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas libres deben ser mayores a 0")
    private Double freeTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de descanso deben ser mayores a 0")
    private Double restHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de tiempo personal deben ser mayores a 0")
    private Double personalTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de tiempo social deben ser mayores a 0")
    private Double socialTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas máximas de estudio por día deben ser mayores a 0")
    @DecimalMax(value = "15.0", message = "Las horas máximas de estudio por día no pueden superar 15")
    private Double maxStudyHoursPerDay;
}
