package com.aibert.dosw.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    private Long id;
    private String studentId;
    private String subjectName;
    private Integer credits;
    private String teacherName;
    private String semester;
    private String schedule;
    private List<EvaluationCut> evaluationCuts;
}
