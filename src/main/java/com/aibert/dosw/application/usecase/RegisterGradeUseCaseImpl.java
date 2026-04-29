package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.CutCapacityExceededException;
import com.aibert.dosw.domain.exceptions.GradeOutOfRangeException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.RegisterGradeUseCase;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterGradeUseCaseImpl implements RegisterGradeUseCase {

    private final SubjectRepositoryPort subjectRepository;
    private final GradeRepositoryPort gradeRepository;

    @Override
    public Grade register(Long subjectId, Long cutId, Grade grade) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        resolveCut(subject, cutId);

        if (grade.getGradeValue() < 0.0 || grade.getGradeValue() > 5.0) {
            throw new GradeOutOfRangeException(grade.getGradeValue());
        }

        List<Grade> existing = gradeRepository.findByCutId(cutId);
        double usedPercentage = existing.stream().mapToDouble(Grade::getPercentage).sum();
        if (usedPercentage + grade.getPercentage() > 100.0 + 0.001) {
            throw new CutCapacityExceededException(usedPercentage, grade.getPercentage());
        }

        Grade toSave = Grade.builder()
                .cutId(cutId)
                .activityName(grade.getActivityName())
                .gradeValue(grade.getGradeValue())
                .percentage(grade.getPercentage())
                .build();

        Grade saved = gradeRepository.save(toSave);

        recalculateCutAverage(subject, cutId);

        return saved;
    }

    @Override
    public List<Grade> getGradesByCut(Long subjectId, Long cutId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        resolveCut(subject, cutId);

        return gradeRepository.findByCutId(cutId);
    }

    // Returns the cut if it belongs to the subject, throws otherwise.
    private EvaluationCut resolveCut(Subject subject, Long cutId) {
        return subject.getEvaluationCuts().stream()
                .filter(c -> c.getId().equals(cutId))
                .findFirst()
                .orElseThrow(() -> new SubjectNotFoundException(subject.getId()));
    }

    private void recalculateCutAverage(Subject subject, Long cutId) {
        List<Grade> grades = gradeRepository.findByCutId(cutId);

        double totalPercentage = grades.stream().mapToDouble(Grade::getPercentage).sum();
        double average = totalPercentage == 0 ? 0.0
                : grades.stream().mapToDouble(g -> g.getGradeValue() * g.getPercentage()).sum()
                  / totalPercentage;

        List<EvaluationCut> updatedCuts = subject.getEvaluationCuts().stream()
                .map(cut -> cut.getId().equals(cutId)
                        ? EvaluationCut.builder()
                                .id(cut.getId())
                                .cutName(cut.getCutName())
                                .cutPercentage(cut.getCutPercentage())
                                .grade(average)
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
                .evaluationCuts(updatedCuts)
                .build());
    }
}
