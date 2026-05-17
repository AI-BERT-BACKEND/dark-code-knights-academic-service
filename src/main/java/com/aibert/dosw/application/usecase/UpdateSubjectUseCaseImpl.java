package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.UpdateSubjectUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateSubjectUseCaseImpl implements UpdateSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public Subject update(Long id, Subject subject) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        validateEvaluationStructure(subject.getEvaluationCuts());

        boolean identityChanged = !existing.getSubjectName().equals(subject.getSubjectName())
                || !existing.getSemester().equals(subject.getSemester());
        if (identityChanged && subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
                existing.getStudentId(), subject.getSubjectName(), subject.getSemester())) {
            throw new DuplicateSubjectException(subject.getSubjectName(), subject.getSemester());
        }

        Subject updated = Subject.builder()
                .id(existing.getId())
                .studentId(existing.getStudentId())
                .subjectName(subject.getSubjectName())
                .credits(subject.getCredits())
                .teacherName(subject.getTeacherName())
                .semester(subject.getSemester())
                .schedule(subject.getSchedule())
                .evaluationCuts(subject.getEvaluationCuts())
                .build();

        return subjectRepository.save(updated);
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
