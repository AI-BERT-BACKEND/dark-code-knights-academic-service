package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicGoal {

    private Long id;
    private String studentId;
    private String goalName;
    private Double targetGrade;
    private Long subjectId; // optional — null means a general (non-subject) goal
}
