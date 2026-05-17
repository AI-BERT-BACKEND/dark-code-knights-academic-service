package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.NoPendingCutsException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.SimulationResult;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.SimulateTargetGradeUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulateTargetGradeUseCaseImpl implements SimulateTargetGradeUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public SimulationResult simulate(Long subjectId, Double targetGrade) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        List<EvaluationCut> pendingCuts = subject.getEvaluationCuts().stream()
                .filter(c -> c.getGrade() == null)
                .toList();

        if (pendingCuts.isEmpty()) {
            throw new NoPendingCutsException(subjectId);
        }

        double currentScore = subject.getEvaluationCuts().stream()
                .filter(c -> c.getGrade() != null)
                .mapToDouble(c -> c.getGrade() * c.getCutPercentage())
                .sum();

        double pendingPercentage = pendingCuts.stream()
                .mapToDouble(EvaluationCut::getCutPercentage)
                .sum();

        double rawRequired = (targetGrade * 100.0 - currentScore) / pendingPercentage;
        boolean achievable = rawRequired <= 5.0;
        double requiredGrade = achievable ? Math.max(0.0, rawRequired) : rawRequired;

        return SimulationResult.builder()
                .targetGrade(targetGrade)
                .requiredGrade(requiredGrade)
                .achievable(achievable)
                .pendingPercentage(pendingPercentage)
                .pendingCuts(pendingCuts)
                .build();
    }
}
