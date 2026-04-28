package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.Subject;

public interface UpdateSubjectUseCase {

    Subject update(Long id, Subject subject);
}
