package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.CreateSubjectUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateSubjectUseCaseImpl implements CreateSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public Subject create(Subject subject) {
        validateEvaluationStructure(subject.getEvaluationCuts());

        if (subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
                subject.getStudentId(), subject.getSubjectName(), subject.getSemester())) {
            throw new DuplicateSubjectException(subject.getSubjectName(), subject.getSemester());
        }

        return subjectRepository.save(subject);
    }

    private void validateEvaluationStructure(List<EvaluationCut> cuts) {
        if (cuts == null || cuts.isEmpty()) {
            throw new InvalidEvaluationStructureException(
                    "La materia debe tener al menos un corte evaluativo");
        }
        double total = cuts.stream().mapToDouble(EvaluationCut::getCutPercentage).sum();
        if (Math.abs(total - 100.0) > 0.001) {
            throw new InvalidEvaluationStructureException(
                    "La suma de porcentajes de los cortes debe ser exactamente 100. Suma actual: " + total);
        }
    }
}
