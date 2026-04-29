package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.GradePersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.EvaluationCutJpaRepository;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GradeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GradeRepositoryAdapter implements GradeRepositoryPort {

    private final GradeJpaRepository gradeJpaRepository;
    private final EvaluationCutJpaRepository evaluationCutJpaRepository;
    private final GradePersistenceMapper persistenceMapper;

    @Override
    public Grade save(Grade grade) {
        EvaluationCutEntity cut = evaluationCutJpaRepository.findById(grade.getCutId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Corte con id " + grade.getCutId() + " no encontrado"));

        GradeEntity entity = persistenceMapper.toEntity(grade);
        entity.setCut(cut);

        return persistenceMapper.toDomain(gradeJpaRepository.save(entity));
    }

    @Override
    public Optional<Grade> findById(Long id) {
        return gradeJpaRepository.findById(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<Grade> findByCutId(Long cutId) {
        return persistenceMapper.toDomainList(gradeJpaRepository.findByCutId(cutId));
    }

    @Override
    public void deleteById(Long id) {
        gradeJpaRepository.deleteById(id);
    }
}
