package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.GradeNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.DeleteGradeUseCase;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteGradeUseCaseImpl implements DeleteGradeUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final GradeRepositoryPort gradeRepository;
    private final AverageCalculator averageCalculator;

    @Override
    public void delete(Long subjectId, Long cutId, Long gradeId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        resolveCut(subject, cutId);

        Grade existing = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new GradeNotFoundException(gradeId));

        if (!existing.getCutId().equals(cutId)) {
            throw new GradeNotFoundException(gradeId);
        }

        gradeRepository.deleteById(gradeId);
        averageCalculator.recalculateCutAverage(subject, cutId);
    }

    private void resolveCut(Subject subject, Long cutId) {
        subject.getEvaluationCuts().stream()
                .filter(c -> c.getId().equals(cutId))
                .findFirst()
                .orElseThrow(() -> new SubjectNotFoundException(subject.getId()));
    }
}
