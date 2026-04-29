package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Subject;

public interface CreateSubjectUseCase {

    Subject create(Subject subject);
}
