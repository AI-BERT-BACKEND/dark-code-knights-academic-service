package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferences {

    private Long id;
    private String studentId;

    /** Preferred time-of-day block for studying. */
    private StudyTime preferredStudyTime;

    /** Preferred study modality. */
    private StudyMethod preferredStudyMethod;

    /** Self-imposed weekly study hours target [1–168]. */
    private Integer weeklyStudyHoursGoal;

    /** Free-text description of the preferred study location (max 100 chars). */
    private String preferredStudyLocation;

    /** Whether the student wants to receive study reminders/notifications. */
    private boolean notificationsEnabled;
}
