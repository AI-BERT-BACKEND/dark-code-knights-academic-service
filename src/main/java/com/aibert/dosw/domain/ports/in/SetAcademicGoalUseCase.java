package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.AcademicGoal;

public interface SetAcademicGoalUseCase {

    AcademicGoal set(String studentId, String goalName, Double targetGrade, Long subjectId);
}
