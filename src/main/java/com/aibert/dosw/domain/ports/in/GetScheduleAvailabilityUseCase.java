package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.ScheduleAvailability;

public interface GetScheduleAvailabilityUseCase {

    ScheduleAvailability get(String studentId);
}
