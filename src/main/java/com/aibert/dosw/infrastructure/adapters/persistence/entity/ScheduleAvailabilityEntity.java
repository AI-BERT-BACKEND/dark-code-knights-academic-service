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
        name = "schedule_availability",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Column(name = "free_time_hours")
    private Double freeTimeHours;

    @Column(name = "rest_hours")
    private Double restHours;

    @Column(name = "personal_time_hours")
    private Double personalTimeHours;

    @Column(name = "social_time_hours")
    private Double socialTimeHours;

    @Column(name = "max_study_hours_per_day")
    private Double maxStudyHoursPerDay;
}
