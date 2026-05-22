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
        name = "academic_goals",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "goal_name"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicGoalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "goal_name", nullable = false, length = 100)
    private String goalName;

    @Column(name = "target_grade", nullable = false)
    private Double targetGrade;

    @Column(name = "subject_id")
    private Long subjectId;
}
