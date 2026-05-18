package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.AcademicGoalProgress;

import java.util.List;

public interface GetAcademicGoalUseCase {

    AcademicGoalProgress getById(Long goalId, String studentId);

    List<AcademicGoalProgress> getAllProgress(String studentId, String semester);
}
