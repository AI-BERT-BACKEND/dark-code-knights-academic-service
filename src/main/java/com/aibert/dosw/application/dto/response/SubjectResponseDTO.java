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
public class SubjectResponseDTO {

    private Long id;
    private String studentId;
    private String subjectName;
    private Integer credits;
    private String teacherName;
    private String semester;
    private String schedule;
    private List<EvaluationCutResponseDTO> evaluationCuts;
}
