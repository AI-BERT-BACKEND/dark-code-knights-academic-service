package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.ScheduleAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleAvailabilityJpaRepository extends JpaRepository<ScheduleAvailabilityEntity, Long> {

    Optional<ScheduleAvailabilityEntity> findByStudentId(String studentId);
}
