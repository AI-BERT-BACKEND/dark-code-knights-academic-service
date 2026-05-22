package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for saving or updating the student's daily schedule availability. All fields are optional. Each value, if provided, must be > 0.")
public class ScheduleAvailabilityRequestDTO {

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas libres deben ser mayores a 0")
    @Schema(example = "4.0", description = "Daily free time hours (> 0.0 if provided)")
    private Double freeTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de descanso deben ser mayores a 0")
    @Schema(example = "8.0", description = "Daily rest/sleep hours (> 0.0 if provided)")
    private Double restHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de tiempo personal deben ser mayores a 0")
    @Schema(example = "2.0", description = "Daily personal time hours (> 0.0 if provided)")
    private Double personalTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas de tiempo social deben ser mayores a 0")
    @Schema(example = "2.0", description = "Daily social time hours (> 0.0 if provided)")
    private Double socialTimeHours;

    @DecimalMin(value = "0.0", inclusive = false, message ="Las horas máximas de estudio por día deben ser mayores a 0")
    @DecimalMax(value = "15.0", message = "Las horas máximas de estudio por día no pueden superar 15")
    @Schema(example = "6.0", description = "Maximum daily study hours (> 0.0 and ≤ 15.0 if provided)")
    private Double maxStudyHoursPerDay;
}
