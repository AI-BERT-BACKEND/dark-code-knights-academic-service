package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleConflictDTO {

    private Long subjectAId;
    private String subjectAName;
    private Long subjectBId;
    private String subjectBName;
    private String conflictingSlot;
}
