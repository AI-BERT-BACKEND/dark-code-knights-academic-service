package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.ports.in.SaveStudyPreferencesUseCase;
import com.aibert.dosw.domain.ports.out.StudyPreferencesRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaveStudyPreferencesUseCaseImpl implements SaveStudyPreferencesUseCase {

    private final StudyPreferencesRepositoryPort preferencesRepository;

    @Override
    public StudyPreferences save(StudyPreferences preferences) {
        Optional<StudyPreferences> existing =
                preferencesRepository.findByStudentId(preferences.getStudentId());

        StudyPreferences toSave = StudyPreferences.builder()
                .id(existing.map(StudyPreferences::getId).orElse(null))
                .studentId(preferences.getStudentId())
                .preferredStudyTime(preferences.getPreferredStudyTime())
                .preferredStudyMethod(preferences.getPreferredStudyMethod())
                .weeklyStudyHoursGoal(preferences.getWeeklyStudyHoursGoal())
                .preferredStudyLocation(preferences.getPreferredStudyLocation())
                .notificationsEnabled(preferences.isNotificationsEnabled())
                .build();

        return preferencesRepository.save(toSave);
    }
}
