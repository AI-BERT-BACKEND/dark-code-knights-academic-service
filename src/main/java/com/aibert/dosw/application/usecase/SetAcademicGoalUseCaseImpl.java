package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.ports.in.SetAcademicGoalUseCase;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SetAcademicGoalUseCaseImpl implements SetAcademicGoalUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final AcademicGoalRepositoryPort goalRepository;

    @Override
    public AcademicGoal set(Long subjectId, String studentId, Double targetGrade) {
        // Validate subject exists and belongs to the student
        subjectRepository.findByIdAndStudentId(subjectId, studentId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        Optional<AcademicGoal> existing =
                goalRepository.findBySubjectIdAndStudentId(subjectId, studentId);

        AcademicGoal goal = AcademicGoal.builder()
                .id(existing.map(AcademicGoal::getId).orElse(null))
                .subjectId(subjectId)
                .studentId(studentId)
                .targetGrade(targetGrade)
                .build();

        return goalRepository.save(goal);
    }
}
