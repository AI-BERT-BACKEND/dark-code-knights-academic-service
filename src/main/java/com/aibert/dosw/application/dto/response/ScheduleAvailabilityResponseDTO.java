package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAvailabilityResponseDTO {

    private Long configId;
    private Double freeTimeHours;
    private Double restHours;
    private Double personalTimeHours;
    private Double socialTimeHours;
    private Double maxStudyHoursPerDay;
}
