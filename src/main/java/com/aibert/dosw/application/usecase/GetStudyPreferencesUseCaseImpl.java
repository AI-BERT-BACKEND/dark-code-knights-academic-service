package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.StudyPreferencesNotFoundException;
import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.ports.in.GetStudyPreferencesUseCase;
import com.aibert.dosw.domain.ports.out.StudyPreferencesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStudyPreferencesUseCaseImpl implements GetStudyPreferencesUseCase {

    private final StudyPreferencesRepositoryPort preferencesRepository;

    @Override
    public StudyPreferences get(String studentId) {
        return preferencesRepository.findByStudentId(studentId)
                .orElseThrow(() -> new StudyPreferencesNotFoundException(studentId));
    }
}
