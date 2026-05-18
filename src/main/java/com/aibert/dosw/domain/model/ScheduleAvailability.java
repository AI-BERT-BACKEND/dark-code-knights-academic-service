package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAvailability {

    private Long id;
    private String studentId;
    private Double freeTimeHours;
    private Double restHours;
    private Double personalTimeHours;
    private Double socialTimeHours;
    private Double maxStudyHoursPerDay;
}
