package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyTime;
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

    private StudyTime preferredStudyTime;

    private StudyMethod preferredStudyMethod;

    @Size(max = 100, message = "El lugar de estudio no puede superar 100 caracteres")
    private String preferredStudyLocation;
}
