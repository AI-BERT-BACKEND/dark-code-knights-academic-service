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
    private String externalId;
    private String studentId;
    private String subjectName;
    private Integer credits;
    private String teacherName;
    private String semester;
    private String schedule;
    private Double overallAverage;
    private List<EvaluationCut> evaluationCuts;

    public Subject(
        Long id,
        String studentId,
        String subjectName,
        Integer credits,
        String teacherName,
        String semester,
        String schedule,
        Double overallAverage,
        List<EvaluationCut> evaluationCuts
    ) {
        this.id = id;
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.teacherName = teacherName;
        this.semester = semester;
        this.schedule = schedule;
        this.overallAverage = overallAverage;
        this.evaluationCuts = evaluationCuts;
    }
}
