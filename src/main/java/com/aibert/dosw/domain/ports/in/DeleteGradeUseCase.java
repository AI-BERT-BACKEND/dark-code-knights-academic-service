package com.aibert.dosw.domain.ports.in;

public interface DeleteGradeUseCase {

    Double delete(Long subjectId, Long cutId, Long gradeId);
}
