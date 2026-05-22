package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.StudyPreferences;

public interface GetStudyPreferencesUseCase {

    /**
     * Returns the study preferences for the given student.
     *
     * @throws com.aibert.dosw.domain.exceptions.StudyPreferencesNotFoundException
     *         if the student has not saved any preferences yet
     */
    StudyPreferences get(String studentId);
}
