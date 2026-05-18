package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.domain.ports.out.ScheduleAvailabilityRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.ScheduleAvailabilityPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.ScheduleAvailabilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScheduleAvailabilityRepositoryAdapter implements ScheduleAvailabilityRepositoryPort {

    private final ScheduleAvailabilityJpaRepository jpaRepository;
    private final ScheduleAvailabilityPersistenceMapper mapper;

    @Override
    public ScheduleAvailability save(ScheduleAvailability availability) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(availability)));
    }

    @Override
    public Optional<ScheduleAvailability> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId).map(mapper::toDomain);
    }
}
