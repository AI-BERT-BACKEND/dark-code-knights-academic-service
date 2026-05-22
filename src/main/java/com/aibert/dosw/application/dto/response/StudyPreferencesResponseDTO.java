package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferencesResponseDTO {

    private Long preferenceId;
    private String studyModality;
    private String studyEnvironment;
    private String studyMethod;
}
