package com.aibert.dosw.domain.ports.in;

import com.aibert.dosw.domain.model.ScheduleConflict;

import java.util.List;

public interface DetectScheduleConflictsUseCase {

    /**
     * Returns every pairwise schedule conflict between the given student's
     * subjects for the specified semester.  Returns an empty list when no
     * conflicts are found.
     *
     * @param studentId the student whose subjects to analyse
     * @param semester  semester in {@code YYYY-1} or {@code YYYY-2} format
     */
    List<ScheduleConflict> detect(String studentId, String semester);
}
