package com.aibert.dosw.domain.ports.in;

public interface DeleteGradeUseCase {

    void delete(Long subjectId, Long cutId, Long gradeId);
}
