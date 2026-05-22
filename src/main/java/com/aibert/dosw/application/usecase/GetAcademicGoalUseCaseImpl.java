package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.GoalNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.AcademicGoalProgress;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicGoalUseCase;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAcademicGoalUseCaseImpl implements GetAcademicGoalUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final AcademicGoalRepositoryPort goalRepository;
    private final AverageCalculator averageCalculator;

    @Override
    public AcademicGoalProgress getById(Long goalId, String studentId) {
        AcademicGoal goal = goalRepository.findById(goalId)
                .filter(g -> studentId.equals(g.getStudentId()))
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        return buildProgress(goal);
    }

    @Override
    public List<AcademicGoalProgress> getAllProgress(String studentId, String semester) {
        List<AcademicGoal> goals = goalRepository.findByStudentId(studentId);
        List<AcademicGoalProgress> result = new ArrayList<>();

        for (AcademicGoal goal : goals) {
            if (goal.getSubjectId() != null) {
                subjectRepository.findById(goal.getSubjectId()).ifPresent(subject -> {
                    if (semester == null || semester.equals(subject.getSemester())) {
                        result.add(buildProgressWithSubject(goal, subject));
                    }
                });
            } else if (semester == null) {
                result.add(buildProgress(goal));
            }
        }

        return result;
    }

    // ─── Progress builders ────────────────────────────────────────────────────

    private AcademicGoalProgress buildProgress(AcademicGoal goal) {
        if (goal.getSubjectId() == null) {
            return AcademicGoalProgress.builder()
                    .goalId(goal.getId())
                    .goalName(goal.getGoalName())
                    .targetGrade(goal.getTargetGrade())
                    .isAchievable(false)
                    .build();
        }
        return subjectRepository.findById(goal.getSubjectId())
                .map(subject -> buildProgressWithSubject(goal, subject))
                .orElseGet(() -> AcademicGoalProgress.builder()
                        .goalId(goal.getId())
                        .goalName(goal.getGoalName())
                        .subjectId(goal.getSubjectId())
                        .targetGrade(goal.getTargetGrade())
                        .isAchievable(false)
                        .build());
    }

    private AcademicGoalProgress buildProgressWithSubject(AcademicGoal goal, Subject subject) {
        List<EvaluationCut> cuts = subject.getEvaluationCuts();

        double pendingPercentage = cuts.stream()
                .filter(c -> c.getGrade() == null)
                .mapToDouble(EvaluationCut::getCutPercentage)
                .sum();

        double currentScore = cuts.stream()
                .filter(c -> c.getGrade() != null)
                .mapToDouble(c -> c.getGrade() * c.getCutPercentage())
                .sum();

        Double currentAverage = averageCalculator.calculateOverallAverage(cuts);

        Double requiredGrade = null;
        boolean isAchievable;

        if (pendingPercentage > 0.001) {
            double raw = (goal.getTargetGrade() * 100.0 - currentScore) / pendingPercentage;
            raw = Math.max(0.0, raw);
            requiredGrade = Math.round(raw * 1000.0) / 1000.0;
            isAchievable = requiredGrade <= 5.0;
        } else {
            isAchievable = currentAverage != null && currentAverage >= goal.getTargetGrade();
        }

        return AcademicGoalProgress.builder()
                .goalId(goal.getId())
                .goalName(goal.getGoalName())
                .subjectId(subject.getId())
                .targetGrade(goal.getTargetGrade())
                .currentAverage(currentAverage)
                .requiredGrade(requiredGrade)
                .isAchievable(isAchievable)
                .build();
    }
}
