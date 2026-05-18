package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferences {

    private Long id;
    private String studentId;
    private StudyTime preferredStudyTime;
    private StudyMethod preferredStudyMethod;
    private String preferredStudyLocation;
}
