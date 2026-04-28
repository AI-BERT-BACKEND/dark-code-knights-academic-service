package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationCutJpaRepository extends JpaRepository<EvaluationCutEntity, Long> {

    List<EvaluationCutEntity> findBySubjectId(Long subjectId);
}
