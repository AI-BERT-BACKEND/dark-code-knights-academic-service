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
    public Optional<AcademicGoal> findBySubjectIdAndStudentId(Long subjectId, String studentId) {
        return goalJpaRepository.findBySubjectIdAndStudentId(subjectId, studentId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<AcademicGoal> findByStudentId(String studentId) {
        return persistenceMapper.toDomainList(
                goalJpaRepository.findByStudentId(studentId));
    }
}
