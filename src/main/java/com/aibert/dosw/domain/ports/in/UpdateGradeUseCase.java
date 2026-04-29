package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Grade;

public interface UpdateGradeUseCase {

    Grade update(Long subjectId, Long cutId, Long gradeId, Grade grade);
}
