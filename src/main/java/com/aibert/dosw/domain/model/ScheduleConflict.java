package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflict {

    private Long subjectAId;
    private String subjectAName;
    private Long subjectBId;
    private String subjectBName;
    private String conflictingSlot;
}
