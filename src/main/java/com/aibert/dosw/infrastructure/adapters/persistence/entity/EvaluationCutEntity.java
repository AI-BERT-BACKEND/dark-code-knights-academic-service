package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluation_cuts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationCutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cut_name", nullable = false)
    private String cutName;

    @Column(name = "cut_percentage", nullable = false)
    private Double cutPercentage;

    @Column
    private Double grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;
}
