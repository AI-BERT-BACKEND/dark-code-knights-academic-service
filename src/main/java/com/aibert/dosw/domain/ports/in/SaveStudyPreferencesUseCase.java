package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.StudyPreferences;

public interface SaveStudyPreferencesUseCase {

    /**
     * Creates or fully replaces the study preferences for the given student.
     * Calling this a second time updates every field of the existing record.
     *
     * @param preferences the preferences to persist (studentId must be set)
     * @return the saved (or updated) preferences
     */
    StudyPreferences save(StudyPreferences preferences);
}
