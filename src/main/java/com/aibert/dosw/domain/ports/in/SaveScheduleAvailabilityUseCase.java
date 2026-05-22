package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.ScheduleAvailability;

public interface SaveScheduleAvailabilityUseCase {

    ScheduleAvailability save(ScheduleAvailability availability);
}
