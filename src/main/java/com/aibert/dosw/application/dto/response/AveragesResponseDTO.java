package com.aibert.dosw.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AveragesResponseDTO {

    private Long subjectId;
    private String subjectName;
    private String semester;
    private Double overallAverage;
    private List<EvaluationCutResponseDTO> cuts;
}
