package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "study_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferencesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_study_time", nullable = false, length = 20)
    private StudyTime preferredStudyTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_study_method", nullable = false, length = 20)
    private StudyMethod preferredStudyMethod;

    @Column(name = "weekly_study_hours_goal", nullable = false)
    private Integer weeklyStudyHoursGoal;

    @Column(name = "preferred_study_location", nullable = false, length = 100)
    private String preferredStudyLocation;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;
}
