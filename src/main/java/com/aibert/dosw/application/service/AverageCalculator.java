package com.aibert.dosw.application.service;

import com.aibert.dosw.application.dto.event.NotificationEvent;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.NotificationEventType;
import com.aibert.dosw.domain.model.NotificationSeverity;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.NotificationProducerPort;
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
    private final NotificationProducerPort notificationProducer;

    /**
     * Calcula el promedio general ponderado de la materia.
     * Fórmula: Σ(cut.grade × cut.cutPercentage) / 100.0
     * Solo considera cortes que ya tienen nota calculada (grade != null).
     *
     * @param evaluationCuts lista completa de cortes de la materia
     * @return promedio ponderado, o null si ningún corte tiene nota
     */
    public Double calculateOverallAverage(List<EvaluationCut> evaluationCuts) {
        boolean anyGraded = evaluationCuts.stream()
                .anyMatch(cut -> cut.getGrade() != null);

        if (!anyGraded) {
            return null;
        }

        return evaluationCuts.stream()
                .filter(cut -> cut.getGrade() != null)
                .mapToDouble(cut -> cut.getGrade() * cut.getCutPercentage())
                .sum() / 100.0;
    }

    public void recalculateCutAverage(Subject subject, Long cutId) {
        List<Grade> grades = gradeRepository.findByCutId(cutId);

        Double previousCutGrade = subject.getEvaluationCuts().stream()
                .filter(c -> c.getId().equals(cutId))
                .findFirst()
                .map(EvaluationCut::getGrade)
                .orElse(null);

        double totalPercentage = grades.stream().mapToDouble(Grade::getPercentage).sum();
        Double newCutGrade = totalPercentage == 0 ? null
                : grades.stream().mapToDouble(g -> g.getGradeValue() * g.getPercentage()).sum()
                  / 100.0;

        List<EvaluationCut> updatedCuts = subject.getEvaluationCuts().stream()
                .map(cut -> cut.getId().equals(cutId)
                        ? EvaluationCut.builder()
                                .id(cut.getId())
                                .cutName(cut.getCutName())
                                .cutPercentage(cut.getCutPercentage())
                                .grade(newCutGrade)
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
                .schedule(subject.getSchedule())
                .evaluationCuts(updatedCuts)
                .build());

        if (previousCutGrade != null && newCutGrade != null && newCutGrade < previousCutGrade) {
            double dropPct = (previousCutGrade - newCutGrade) / previousCutGrade * 100.0;
            notificationProducer.publish(NotificationEvent.builder()
                    .userId(subject.getStudentId())
                    .type(NotificationEventType.LOW_PERFORMANCE_ALERT)
                    .title("Rendimiento bajo detectado")
                    .message(String.format("Tu promedio en %s ha bajado un %.0f%% en el corte actual",
                            subject.getSubjectName(), dropPct))
                    .severity(resolveSeverity(dropPct))
                    .relatedEntityId(subject.getId())
                    .build());
        }
    }

    private NotificationSeverity resolveSeverity(double dropPct) {
        if (dropPct >= 20.0) return NotificationSeverity.CRITICAL;
        if (dropPct >= 10.0) return NotificationSeverity.HIGH;
        if (dropPct >= 5.0)  return NotificationSeverity.MEDIUM;
        return NotificationSeverity.LOW;
    }
}
