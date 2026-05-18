package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Transient (never persisted) — enriches {@link AcademicGoal} with
 * computed progress data derived from the subject's evaluation cuts.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicGoalProgress {

    private Long goalId;
    private Long subjectId;
    private String subjectName;
    private String semester;
    private Double targetGrade;

    /** Weighted average so far; {@code null} when no cut has been graded yet. */
    private Double currentAverage;

    /**
     * Grade needed across all pending cuts to still reach {@code targetGrade}.
     * {@code null} when there are no pending cuts left.
     */
    private Double requiredGrade;

    /**
     * Whether the target can still be achieved:
     * <ul>
     *   <li>Pending cuts present: {@code requiredGrade <= 5.0}</li>
     *   <li>All cuts graded: {@code currentAverage >= targetGrade}</li>
     * </ul>
     */
    private boolean isAchievable;
}
