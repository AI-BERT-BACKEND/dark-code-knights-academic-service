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
    private Long subjectId;
    private String subjectName;
    private String semester;
    private Double targetGrade;

    /** Weighted average so far; null when no cut has been graded yet. */
    private Double currentAverage;

    /**
     * Grade needed across all pending cuts to reach the target.
     * Null when all cuts are already graded.
     */
    private Double requiredGrade;

    /** Whether the target can still be achieved given the current state. */
    private boolean isAchievable;
}
