package com.aibert.dosw.application.service;

import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AverageCalculator {

    private final SubjectRepositoryPort subjectRepository;
    private final GradeRepositoryPort gradeRepository;

    public void recalculateCutAverage(Subject subject, Long cutId) {
        List<Grade> grades = gradeRepository.findByCutId(cutId);

        double totalPercentage = grades.stream().mapToDouble(Grade::getPercentage).sum();
        Double average = totalPercentage == 0 ? null
                : grades.stream().mapToDouble(g -> g.getGradeValue() * g.getPercentage()).sum()
                  / totalPercentage;

        List<EvaluationCut> updatedCuts = subject.getEvaluationCuts().stream()
                .map(cut -> cut.getId().equals(cutId)
                        ? EvaluationCut.builder()
                                .id(cut.getId())
                                .cutName(cut.getCutName())
                                .cutPercentage(cut.getCutPercentage())
                                .grade(average)
                                .build()
                        : cut)
                .collect(Collectors.toList());

        subjectRepository.save(Subject.builder()
                .id(subject.getId())
                .studentId(subject.getStudentId())
                .subjectName(subject.getSubjectName())
                .credits(subject.getCredits())
                .teacherName(subject.getTeacherName())
                .semester(subject.getSemester())
                .evaluationCuts(updatedCuts)
                .build());
    }
}
