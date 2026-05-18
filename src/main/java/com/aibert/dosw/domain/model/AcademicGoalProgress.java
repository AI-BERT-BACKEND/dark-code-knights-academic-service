package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicGoalProgress {

    private Long goalId;
    private String goalName;
    private Long subjectId;
    private String subjectName;
    private String semester;
    private Double targetGrade;
    private Double currentAverage;
    private Double requiredGrade;
    private boolean isAchievable;
}
