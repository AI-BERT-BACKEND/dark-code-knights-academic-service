package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebugConfigureTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private ConfigureEvaluationStructureUseCaseImpl configureEvaluationStructureUseCase;

    @Test
    void debugValidation() {
        // Setup subject without grades
        Subject subjectWithoutGrades = Subject.builder()
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

        // Invalid cuts that sum to 70.0
        List<EvaluationCut> invalidCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(30.0)
                .build()
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));

        // Debug: Check the sum
        double total = invalidCuts.stream().mapToDouble(EvaluationCut::getCutPercentage).sum();
        System.out.println("Invalid cuts total: " + total);
        System.out.println("Should throw exception: " + (Math.abs(total - 100.0) > 0.001));

        // This should throw InvalidEvaluationStructureException
        try {
            configureEvaluationStructureUseCase.configure(1L, invalidCuts);
            System.out.println("ERROR: No exception was thrown!");
            fail("Expected InvalidEvaluationStructureException but none was thrown");
        } catch (Exception e) {
            System.out.println("SUCCESS: Exception thrown: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
            assertTrue(e instanceof InvalidEvaluationStructureException);
        }
    }
}
