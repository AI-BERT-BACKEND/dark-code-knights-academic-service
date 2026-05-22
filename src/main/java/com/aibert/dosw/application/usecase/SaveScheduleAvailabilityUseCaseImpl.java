package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.ScheduleHoursExceedDayException;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.in.SaveScheduleAvailabilityUseCase;
import com.aibert.dosw.domain.ports.out.ScheduleAvailabilityRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveScheduleAvailabilityUseCaseImpl implements SaveScheduleAvailabilityUseCase {

    private static final double MAX_HOURS_PER_DAY = 24.0;

    private final ScheduleAvailabilityRepositoryPort repository;

    @Override
    public ScheduleAvailability save(ScheduleAvailability incoming) {
        double total = sum(incoming);
        if (total > MAX_HOURS_PER_DAY) {
            throw new ScheduleHoursExceedDayException(total);
        }

        Long existingId = repository.findByStudentId(incoming.getStudentId())
                .map(ScheduleAvailability::getId)
                .orElse(null);

        ScheduleAvailability toSave = ScheduleAvailability.builder()
                .id(existingId)
                .studentId(incoming.getStudentId())
                .freeTimeHours(incoming.getFreeTimeHours())
                .restHours(incoming.getRestHours())
                .personalTimeHours(incoming.getPersonalTimeHours())
                .socialTimeHours(incoming.getSocialTimeHours())
                .maxStudyHoursPerDay(incoming.getMaxStudyHoursPerDay())
                .build();

        return repository.save(toSave);
    }

    private double sum(ScheduleAvailability a) {
        return nullSafe(a.getFreeTimeHours())
                + nullSafe(a.getRestHours())
                + nullSafe(a.getPersonalTimeHours())
                + nullSafe(a.getSocialTimeHours())
                + nullSafe(a.getMaxStudyHoursPerDay());
    }

    private double nullSafe(Double value) {
        return value == null ? 0.0 : value;
    }
}
