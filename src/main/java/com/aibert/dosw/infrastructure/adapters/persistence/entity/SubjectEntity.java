package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "subjects",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"student_id", "subject_name", "semester"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true, nullable = false, length = 36)
    private String externalId;

    @PrePersist
    private void generateExternalId() {
        if (externalId == null) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;

    @Column(nullable = false)
    private Integer credits;

    @Column(name = "teacher_name", nullable = false, length = 100)
    private String teacherName;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String schedule;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EvaluationCutEntity> evaluationCuts;

    public SubjectEntity(
        Long id,
        String studentId,
        String subjectName,
        Integer credits,
        String teacherName,
        String semester,
        String schedule,
        List<EvaluationCutEntity> evaluationCuts
    ) {
        this.id = id;
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.teacherName = teacherName;
        this.semester = semester;
        this.schedule = schedule;
        this.evaluationCuts = evaluationCuts;
    }
}
