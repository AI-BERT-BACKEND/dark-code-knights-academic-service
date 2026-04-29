package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.CutCapacityExceededException;
import com.aibert.dosw.domain.exceptions.GradeNotFoundException;
import com.aibert.dosw.domain.exceptions.GradeOutOfRangeException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.UpdateGradeUseCase;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateGradeUseCaseImpl implements UpdateGradeUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final GradeRepositoryPort gradeRepository;
    private final AverageCalculator averageCalculator;

    @Override
    public Grade update(Long subjectId, Long cutId, Long gradeId, Grade grade) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        resolveCut(subject, cutId);

        Grade existing = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new GradeNotFoundException(gradeId));

        if (!existing.getCutId().equals(cutId)) {
            throw new GradeNotFoundException(gradeId);
        }

        if (grade.getGradeValue() < 0.0 || grade.getGradeValue() > 5.0) {
            throw new GradeOutOfRangeException(grade.getGradeValue());
        }

        if (!existing.getPercentage().equals(grade.getPercentage())) {
            List<Grade> others = gradeRepository.findByCutId(cutId).stream()
                    .filter(g -> !g.getId().equals(gradeId))
                    .collect(Collectors.toList());
            double usedByOthers = others.stream().mapToDouble(Grade::getPercentage).sum();
            if (usedByOthers + grade.getPercentage() > 100.0 + 0.001) {
                throw new CutCapacityExceededException(usedByOthers, grade.getPercentage());
            }
        }

        Grade updated = Grade.builder()
                .id(gradeId)
                .cutId(cutId)
                .activityName(grade.getActivityName())
                .gradeValue(grade.getGradeValue())
                .percentage(grade.getPercentage())
                .build();

        Grade saved = gradeRepository.save(updated);
        averageCalculator.recalculateCutAverage(subject, cutId);
        return saved;
    }

    private void resolveCut(Subject subject, Long cutId) {
        subject.getEvaluationCuts().stream()
                .filter(c -> c.getId().equals(cutId))
                .findFirst()
                .orElseThrow(() -> new SubjectNotFoundException(subject.getId()));
    }
}
