package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.ScheduleAvailability;

import java.util.Optional;

public interface ScheduleAvailabilityRepositoryPort {

    ScheduleAvailability save(ScheduleAvailability availability);

    Optional<ScheduleAvailability> findByStudentId(String studentId);
}
