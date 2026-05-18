package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicGoalProgressDTO {

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
