package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.StudyPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyPreferencesJpaRepository extends JpaRepository<StudyPreferencesEntity, Long> {

    Optional<StudyPreferencesEntity> findByStudentId(String studentId);
}
