package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Subject;

import java.util.List;

public interface GetSubjectsUseCase {

    List<Subject> getAllByStudent(String studentId);

    Subject getById(Long id);
}
