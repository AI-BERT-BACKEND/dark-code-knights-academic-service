package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.AcademicGoal;

import java.util.List;
import java.util.Optional;

public interface AcademicGoalRepositoryPort {

    AcademicGoal save(AcademicGoal goal);

    Optional<AcademicGoal> findBySubjectIdAndStudentId(Long subjectId, String studentId);

    List<AcademicGoal> findByStudentId(String studentId);
}
