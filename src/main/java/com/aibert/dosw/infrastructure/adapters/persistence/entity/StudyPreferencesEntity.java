package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "study_modality", length = 100)
    private String studyModality;

    @Column(name = "study_environment", length = 100)
    private String studyEnvironment;

    @Column(name = "study_method", length = 100)
    private String studyMethod;
}
