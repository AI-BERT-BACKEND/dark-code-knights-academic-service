package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeRepositoryPort {

    Grade save(Grade grade);

    Optional<Grade> findById(Long id);

    List<Grade> findByCutId(Long cutId);

    void deleteById(Long id);
}
