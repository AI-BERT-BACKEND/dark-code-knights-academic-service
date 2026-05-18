package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.AcademicGoalProgress;

import java.util.List;

public interface GetAcademicGoalUseCase {

    /**
     * Returns the goal and live progress for a single subject.
     *
     * @throws com.aibert.dosw.domain.exceptions.GoalNotFoundException   if no goal exists for this subject
     * @throws com.aibert.dosw.domain.exceptions.SubjectNotFoundException if the subject does not exist
     */
    AcademicGoalProgress getProgress(Long subjectId, String studentId);

    /**
     * Returns the goal and live progress for every subject the student
     * has a goal for in the given semester.  Returns an empty list when
     * no goals are found.
     */
    List<AcademicGoalProgress> getAllProgress(String studentId, String semester);
}
