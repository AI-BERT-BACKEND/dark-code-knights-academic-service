package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.ports.in.SetAcademicGoalUseCase;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetAcademicGoalUseCaseImpl implements SetAcademicGoalUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final AcademicGoalRepositoryPort goalRepository;

    @Override
    public AcademicGoal set(String studentId, String goalName, Double targetGrade, Long subjectId) {
        if (subjectId != null) {
            subjectRepository.findByIdAndStudentId(subjectId, studentId)
                    .orElseThrow(() -> new SubjectNotFoundException(subjectId));
        }

        Long existingId = goalRepository.findByStudentIdAndGoalName(studentId, goalName)
                .map(AcademicGoal::getId)
                .orElse(null);

        AcademicGoal goal = AcademicGoal.builder()
                .id(existingId)
                .studentId(studentId)
                .goalName(goalName)
                .targetGrade(targetGrade)
                .subjectId(subjectId)
                .build();

        return goalRepository.save(goal);
    }
}
