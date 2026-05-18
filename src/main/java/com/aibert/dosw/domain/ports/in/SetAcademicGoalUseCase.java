package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.AcademicGoal;

public interface SetAcademicGoalUseCase {

    /**
     * Creates or replaces the academic goal for a given subject.
     * Only one goal is allowed per student+subject; calling this a second time
     * updates the existing goal with the new {@code targetGrade}.
     *
     * @param subjectId   the subject the goal applies to
     * @param studentId   owner of the goal (from the request header)
     * @param targetGrade desired final grade [0.0 – 5.0]
     * @return the persisted (or updated) goal
     */
    AcademicGoal set(Long subjectId, String studentId, Double targetGrade);
}
