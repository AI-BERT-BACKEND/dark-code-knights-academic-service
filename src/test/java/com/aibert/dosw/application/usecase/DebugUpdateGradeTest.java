package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import com.aibert.dosw.application.service.AverageCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebugUpdateGradeTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private UpdateGradeUseCaseImpl updateGradeUseCase;

    @Test
    void debugCapacityValidation() {
        // Setup subject
        Subject subject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(
                EvaluationCut.builder()
                    .id(1L)
                    .cutName("Corte 1")
                    .cutPercentage(40.0)
                    .grade(null)
                    .build()
            ))
            .build();

        // Existing grade with 60.0%
        Grade existingGrade = Grade.builder()
            .id(1L)
            .cutId(1L)
            .activityName("Parcial 1")
            .gradeValue(4.5)
            .percentage(60.0)
            .build();

        // Other grade with 50.0%
        Grade otherGrade = Grade.builder()
            .id(2L)
            .cutId(1L)
            .activityName("Quiz 1")
            .gradeValue(3.0)
            .percentage(50.0)
            .build();

        // New grade with increased percentage to 60.0%
        Grade gradeWithIncreasedPercentage = Grade.builder()
            .activityName("Parcial 1")
            .gradeValue(4.5)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(60.0)
                .build()
        );

        // Debug: Check the values
        System.out.println("Existing grade percentage: " + existingGrade.getPercentage());
        System.out.println("New grade percentage: " + gradeWithIncreasedPercentage.getPercentage());
        System.out.println("Are they equal? " + existingGrade.getPercentage().equals(gradeWithIncreasedPercentage.getPercentage()));
        System.out.println("Other grade percentage: " + otherGrade.getPercentage());
        
        double usedByOthers = 50.0; // otherGrade percentage
        double newPercentage = 60.0;
        System.out.println("Used by others: " + usedByOthers);
        System.out.println("New percentage: " + newPercentage);
        System.out.println("Total would be: " + (usedByOthers + newPercentage));
        System.out.println("Should throw exception: " + (usedByOthers + newPercentage > 100.0));

        // This should NOT throw CutCapacityExceededException because percentage doesn't change
        try {
            Grade result = updateGradeUseCase.update(1L, 1L, 1L, gradeWithIncreasedPercentage);
            System.out.println("SUCCESS: Grade updated without exception");
            assertNotNull(result);
            assertEquals("Parcial 1", result.getActivityName());
            assertEquals(4.5, result.getGradeValue());
            assertEquals(60.0, result.getPercentage());
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected exception thrown: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
            fail("No exception should be thrown when percentage doesn't change");
        }
    }
}
