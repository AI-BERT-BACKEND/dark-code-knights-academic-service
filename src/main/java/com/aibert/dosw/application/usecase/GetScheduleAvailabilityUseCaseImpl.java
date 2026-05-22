package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.ScheduleAvailabilityNotFoundException;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.in.GetScheduleAvailabilityUseCase;
import com.aibert.dosw.domain.ports.out.ScheduleAvailabilityRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetScheduleAvailabilityUseCaseImpl implements GetScheduleAvailabilityUseCase {

    private final ScheduleAvailabilityRepositoryPort repository;

    @Override
    public ScheduleAvailability get(String studentId) {
        return repository.findByStudentId(studentId)
                .orElseThrow(() -> new ScheduleAvailabilityNotFoundException(studentId));
    }
}
