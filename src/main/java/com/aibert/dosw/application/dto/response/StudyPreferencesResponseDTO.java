package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferencesResponseDTO {

    private Long id;
    private String studentId;
    private StudyTime preferredStudyTime;
    private StudyMethod preferredStudyMethod;
    private String preferredStudyLocation;
}
