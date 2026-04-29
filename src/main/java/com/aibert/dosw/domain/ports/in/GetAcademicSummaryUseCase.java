package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Subject;

import java.util.List;

public interface GetAcademicSummaryUseCase {

    List<Subject> getSummary(String studentId);
}
