package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.SimulationResult;

public interface SimulateTargetGradeUseCase {

    SimulationResult simulate(Long subjectId, Double targetGrade);
}
