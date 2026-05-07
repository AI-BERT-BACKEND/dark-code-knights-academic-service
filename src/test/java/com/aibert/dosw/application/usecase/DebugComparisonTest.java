package com.aibert.dosw.application.usecase;

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
class DebugComparisonTest {

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
    void testValidSubjectFirst() {
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(validSubject);

        Subject result = createSubjectUseCase.create(validSubject);
        assertNotNull(result);
    }

    @Test
    void testInvalidSubjectAfterValid() {
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

        // This should throw InvalidEvaluationStructureException
        assertThrows(Exception.class, () -> {
            createSubjectUseCase.create(subjectWithInvalidPercentages);
        });
    }
}
