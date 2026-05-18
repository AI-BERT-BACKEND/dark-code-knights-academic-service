package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.SubjectPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.SubjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectRepositoryAdapter implements SubjectRepositoryPort {

    private final SubjectJpaRepository subjectJpaRepository;
    private final SubjectPersistenceMapper persistenceMapper;

    @Override
    public Subject save(Subject subject) {
        SubjectEntity entity = persistenceMapper.toEntity(subject);

        List<EvaluationCutEntity> cuts = subject.getEvaluationCuts().stream()
                .map(cut -> {
                    EvaluationCutEntity cutEntity = persistenceMapper.toEntity(cut);
                    cutEntity.setSubject(entity);
                    return cutEntity;
                })
                .collect(Collectors.toList());

        entity.setEvaluationCuts(cuts);

        return persistenceMapper.toDomain(subjectJpaRepository.save(entity));
    }

    @Override
    public Optional<Subject> findById(Long id) {
        return subjectJpaRepository.findById(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<Subject> findByStudentId(String studentId) {
        return persistenceMapper.toDomainList(subjectJpaRepository.findByStudentId(studentId));
    }

    @Override
    public boolean existsByStudentIdAndSubjectNameAndSemester(String studentId, String subjectName, String semester) {
        return subjectJpaRepository.existsByStudentIdAndSubjectNameAndSemester(studentId, subjectName, semester);
    }

    @Override
    public Optional<Subject> findByIdAndStudentId(Long id, String studentId) {
        return subjectJpaRepository.findByIdAndStudentId(id, studentId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        subjectJpaRepository.deleteById(id);
    }

    @Override
    public List<Subject> findByStudentIdAndSemester(String studentId, String semester) {
        return persistenceMapper.toDomainList(
                subjectJpaRepository.findByStudentIdAndSemester(studentId, semester));
    }
}
