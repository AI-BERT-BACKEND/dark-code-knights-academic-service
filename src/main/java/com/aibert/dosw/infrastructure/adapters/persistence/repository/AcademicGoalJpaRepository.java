package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.AcademicGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicGoalJpaRepository extends JpaRepository<AcademicGoalEntity, Long> {

    Optional<AcademicGoalEntity> findBySubjectIdAndStudentId(Long subjectId, String studentId);

    List<AcademicGoalEntity> findByStudentId(String studentId);
}
