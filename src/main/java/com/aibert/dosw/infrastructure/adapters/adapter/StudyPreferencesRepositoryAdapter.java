package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.ports.out.StudyPreferencesRepositoryPort;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.StudyPreferencesPersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.StudyPreferencesJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyPreferencesRepositoryAdapter implements StudyPreferencesRepositoryPort {

    private final StudyPreferencesJpaRepository jpaRepository;
    private final StudyPreferencesPersistenceMapper persistenceMapper;

    @Override
    public StudyPreferences save(StudyPreferences preferences) {
        return persistenceMapper.toDomain(
                jpaRepository.save(persistenceMapper.toEntity(preferences)));
    }

    @Override
    public Optional<StudyPreferences> findByStudentId(String studentId) {
        return jpaRepository.findByStudentId(studentId)
                .map(persistenceMapper::toDomain);
    }
}
