package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.StudyPreferences;

import java.util.Optional;

public interface StudyPreferencesRepositoryPort {

    StudyPreferences save(StudyPreferences preferences);

    Optional<StudyPreferences> findByStudentId(String studentId);
}
