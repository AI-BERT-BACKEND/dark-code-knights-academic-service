package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailedDebugTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private CreateSubjectUseCaseImpl createSubjectUseCase;

    private Subject validSubject;
    private List<EvaluationCut> validEvaluationCuts;

    @BeforeEach
    void setUp() {
        validEvaluationCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(60.0)
                .build()
        );

        validSubject = Subject.builder()
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(validEvaluationCuts)
            .build();
    }

    @Test
    void debugStepByStep() {
        System.out.println("=== DEBUG STEP BY STEP ===");
        
        // Step 1: Test valid subject first (like original test)
        System.out.println("Step 1: Testing valid subject...");
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(validSubject);

        Subject result = createSubjectUseCase.create(validSubject);
        assertNotNull(result);
        System.out.println("Valid subject test PASSED");

        // Step 2: Test invalid subject (like failing test)
        System.out.println("Step 2: Testing invalid subject...");
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

        Subject subjectWithInvalidPercentages = Subject.builder()
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(invalidCuts)
            .build();

        // Debug: Check the sum
        double total = invalidCuts.stream().mapToDouble(EvaluationCut::getCutPercentage).sum();
        System.out.println("Invalid cuts total: " + total);
        System.out.println("Should throw exception: " + (Math.abs(total - 100.0) > 0.001));

        // This should throw InvalidEvaluationStructureException
        try {
            createSubjectUseCase.create(subjectWithInvalidPercentages);
            System.out.println("ERROR: No exception was thrown!");
            fail("Expected InvalidEvaluationStructureException but none was thrown");
        } catch (Exception e) {
            System.out.println("SUCCESS: Exception thrown: " + e.getClass().getSimpleName());
            System.out.println("Message: " + e.getMessage());
            assertTrue(e instanceof InvalidEvaluationStructureException);
        }
    }
}
