package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.EvaluationStructureLockedException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.ConfigureEvaluationStructureUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigureEvaluationStructureUseCaseImpl implements ConfigureEvaluationStructureUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public List<EvaluationCut> configure(Long subjectId, List<EvaluationCut> newCuts) {
        Subject existing = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        boolean hasGrades = existing.getEvaluationCuts().stream()
                .anyMatch(cut -> cut.getGrade() != null);
        if (hasGrades) {
            throw new EvaluationStructureLockedException(subjectId);
        }

        validateEvaluationStructure(newCuts);

        Subject updated = Subject.builder()
                .id(existing.getId())
                .externalId(existing.getExternalId())
                .studentId(existing.getStudentId())
                .subjectName(existing.getSubjectName())
                .credits(existing.getCredits())
                .teacherName(existing.getTeacherName())
                .semester(existing.getSemester())
                .schedule(existing.getSchedule())
                .evaluationCuts(newCuts)
                .build();

        return subjectRepository.save(updated).getEvaluationCuts();
    }

    @Override
    public List<EvaluationCut> getStructure(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId))
                .getEvaluationCuts();
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
