package com.aibert.dosw.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPreferencesRequestDTO {

    private String studyModality;

    private String studyEnvironment;

    @Size(max = 100, message = "El método de estudio no puede superar 100 caracteres")
    private String studyMethod;
}
