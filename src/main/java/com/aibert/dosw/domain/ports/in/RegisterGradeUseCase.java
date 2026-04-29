package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Grade;

import java.util.List;

public interface RegisterGradeUseCase {

    Grade register(Long subjectId, Long cutId, Grade grade);

    List<Grade> getGradesByCut(Long subjectId, Long cutId);
}
