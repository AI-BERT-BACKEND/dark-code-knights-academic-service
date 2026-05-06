package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSubjectUseCase Tests")
class CreateSubjectUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private CreateSubjectUseCaseImpl createSubjectUseCase;

    private Subject validSubject;

    @BeforeEach
    void setUp() {
        List<EvaluationCut> validEvaluationCuts = Arrays.asList(
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
    @DisplayName("Should create subject successfully with valid data")
    void shouldCreateSubjectSuccessfullyWithValidData() {
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(validSubject);

        Subject result = createSubjectUseCase.create(validSubject);

        assertNotNull(result);
        assertEquals("Cálculo Diferencial", result.getSubjectName());
        assertEquals("student-test", result.getStudentId());
        assertEquals("2025-1", result.getSemester());
        
        verify(subjectRepository).existsByStudentIdAndSubjectNameAndSemester("student-test", "Cálculo Diferencial", "2025-1");
        verify(subjectRepository).save(validSubject);
    }

    @Test
    @DisplayName("Should throw DuplicateSubjectException when subject already exists")
    void shouldThrowDuplicateSubjectExceptionWhenSubjectAlreadyExists() {
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(true);

        DuplicateSubjectException exception = assertThrows(
            DuplicateSubjectException.class,
            () -> createSubjectUseCase.create(validSubject)
        );

        assertTrue(exception.getMessage().contains("Cálculo Diferencial"));
        assertTrue(exception.getMessage().contains("2025-1"));
        
        verify(subjectRepository).existsByStudentIdAndSubjectNameAndSemester("student-test", "Cálculo Diferencial", "2025-1");
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is null")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsIsNull() {
        Subject subjectWithNullCuts = Subject.builder()
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(null)
            .build();

        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithNullCuts)
        );

        assertTrue(exception.getMessage().contains("debe tener al menos un corte evaluativo"));
        
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is empty")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsIsEmpty() {
        Subject subjectWithEmptyCuts = Subject.builder()
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();

        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithEmptyCuts)
        );

        assertTrue(exception.getMessage().contains("debe tener al menos un corte evaluativo"));
        
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentages sum less than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentagesSumLessThan100() {
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

        // Test without any mock configuration to ensure validation works
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithInvalidPercentages)
        );

        assertTrue(exception.getMessage().contains("La suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("70.0"));
        
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentages sum more than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentagesSumMoreThan100() {
        List<EvaluationCut> invalidCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(60.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(50.0)
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

        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithInvalidPercentages)
        );

        assertTrue(exception.getMessage().contains("La suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("110.0"));
        
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should create subject successfully with single evaluation cut")
    void shouldCreateSubjectSuccessfullyWithSingleEvaluationCut() {
        List<EvaluationCut> singleCut = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte Único")
                .cutPercentage(100.0)
                .build()
        );

        Subject subjectWithSingleCut = Subject.builder()
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(3)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(singleCut)
            .build();

        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithSingleCut);

        Subject result = createSubjectUseCase.create(subjectWithSingleCut);

        assertNotNull(result);
        assertEquals("Materia Simple", result.getSubjectName());
        assertEquals(1, result.getEvaluationCuts().size());
        
        verify(subjectRepository).existsByStudentIdAndSubjectNameAndSemester("student-test", "Materia Simple", "2025-1");
        verify(subjectRepository).save(subjectWithSingleCut);
    }

    @Test
    @DisplayName("Should handle decimal precision in percentage calculation")
    void shouldHandleDecimalPrecisionInPercentageCalculation() {
        List<EvaluationCut> preciseCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(33.33)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(33.33)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 3")
                .cutPercentage(33.34)
                .build()
        );

        Subject subjectWithPreciseCuts = Subject.builder()
            .studentId("student-test")
            .subjectName("Materia Precisa")
            .credits(4)
            .teacherName("Prof. Preciso")
            .semester("2025-1")
            .evaluationCuts(preciseCuts)
            .build();

        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(anyString(), anyString(), anyString()))
            .thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithPreciseCuts);

        Subject result = createSubjectUseCase.create(subjectWithPreciseCuts);

        assertNotNull(result);
        assertEquals("Materia Precisa", result.getSubjectName());
        
        verify(subjectRepository).existsByStudentIdAndSubjectNameAndSemester("student-test", "Materia Precisa", "2025-1");
        verify(subjectRepository).save(subjectWithPreciseCuts);
    }
}
