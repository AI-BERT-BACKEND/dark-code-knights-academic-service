package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.AcademicGoalPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.AcademicGoalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AcademicGoalRepositoryAdapter implements AcademicGoalRepositoryPort {

    private final AcademicGoalJpaRepository goalJpaRepository;
    private final AcademicGoalPersistenceMapper persistenceMapper;

    @Override
    public AcademicGoal save(AcademicGoal goal) {
        return persistenceMapper.toDomain(
                goalJpaRepository.save(persistenceMapper.toEntity(goal)));
    }

    @Override
    public Optional<AcademicGoal> findById(Long id) {
        return goalJpaRepository.findById(id).map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<AcademicGoal> findByStudentIdAndGoalName(String studentId, String goalName) {
        return goalJpaRepository.findByStudentIdAndGoalName(studentId, goalName)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<AcademicGoal> findByStudentId(String studentId) {
        return persistenceMapper.toDomainList(goalJpaRepository.findByStudentId(studentId));
    }
}
