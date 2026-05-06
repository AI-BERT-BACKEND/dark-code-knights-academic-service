package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.NoPendingCutsException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.SimulationResult;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulateTargetGradeUseCase Tests")
class SimulateTargetGradeUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private SimulateTargetGradeUseCaseImpl simulateTargetGradeUseCase;

    private Subject subject;
    private List<EvaluationCut> evaluationCuts;

    @BeforeEach
    void setUp() {
        evaluationCuts = Arrays.asList(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(5.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(30.0)
                .grade(3.5)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(40.0)
                .grade(null)
                .build()
        );

        subject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();
    }

    @Test
    @DisplayName("Should simulate achievable target grade successfully")
    void shouldSimulateAchievableTargetGradeSuccessfully() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 4.0);

        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertEquals(40.0, result.getPendingPercentage());
        assertTrue(result.isAchievable());
        
        double expectedRequired = (4.0 * 100.0 - 255.0) / 40.0;
        assertEquals(expectedRequired, result.getRequiredGrade(), 0.01);

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should simulate unachievable target grade successfully")
    void shouldSimulateUnachievableTargetGradeSuccessfully() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 5.0);

        assertNotNull(result);
        assertEquals(5.0, result.getTargetGrade());
        assertEquals(40.0, result.getPendingPercentage());
        assertFalse(result.isAchievable());
        
        double expectedRequired = (5.0 * 100.0 - 255.0) / 40.0;
        assertEquals(expectedRequired, result.getRequiredGrade(), 0.01);
        assertTrue(result.getRequiredGrade() > 5.0);

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should simulate already achieved target grade successfully")
    void shouldSimulateAlreadyAchievedTargetGradeSuccessfully() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 2.0);

        assertNotNull(result);
        assertEquals(2.0, result.getTargetGrade());
        assertEquals(40.0, result.getPendingPercentage());
        assertTrue(result.isAchievable());
        assertEquals(0.0, result.getRequiredGrade());

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not exist")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> simulateTargetGradeUseCase.simulate(1L, 4.0)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NoPendingCutsException when all cuts have grades")
    void shouldThrowNoPendingCutsExceptionWhenAllCutsHaveGrades() {
        Subject subjectWithAllGrades = Subject.builder()
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
                    .cutPercentage(30.0)
                    .grade(5.0)
                    .build(),
                EvaluationCut.builder()
                    .id(2L)
                    .cutName("Corte 2")
                    .cutPercentage(30.0)
                    .grade(3.5)
                    .build(),
                EvaluationCut.builder()
                    .id(3L)
                    .cutName("Corte 3")
                    .cutPercentage(40.0)
                    .grade(4.0)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithAllGrades));

        NoPendingCutsException exception = assertThrows(
            NoPendingCutsException.class,
            () -> simulateTargetGradeUseCase.simulate(1L, 4.0)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle subject with no graded cuts")
    void shouldHandleSubjectWithNoGradedCuts() {
        Subject subjectWithNoGrades = Subject.builder()
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
                    .cutPercentage(30.0)
                    .grade(null)
                    .build(),
                EvaluationCut.builder()
                    .id(2L)
                    .cutName("Corte 2")
                    .cutPercentage(30.0)
                    .grade(null)
                    .build(),
                EvaluationCut.builder()
                    .id(3L)
                    .cutName("Corte 3")
                    .cutPercentage(40.0)
                    .grade(null)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithNoGrades));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 3.5);

        assertNotNull(result);
        assertEquals(3.5, result.getTargetGrade());
        assertEquals(100.0, result.getPendingPercentage());
        assertTrue(result.isAchievable());
        assertEquals(3.5, result.getRequiredGrade(), 0.01);

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle subject with single pending cut")
    void shouldHandleSubjectWithSinglePendingCut() {
        Subject subjectWithSinglePending = Subject.builder()
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
                    .cutPercentage(60.0)
                    .grade(4.0)
                    .build(),
                EvaluationCut.builder()
                    .id(2L)
                    .cutName("Corte 2")
                    .cutPercentage(40.0)
                    .grade(null)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithSinglePending));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 3.5);

        assertNotNull(result);
        assertEquals(3.5, result.getTargetGrade());
        assertEquals(40.0, result.getPendingPercentage());
        assertTrue(result.isAchievable());
        
        double expectedRequired = (3.5 * 100.0 - 240.0) / 40.0;
        assertEquals(expectedRequired, result.getRequiredGrade(), 0.01);

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle boundary target grades")
    void shouldHandleBoundaryTargetGrades() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SimulationResult zeroResult = simulateTargetGradeUseCase.simulate(1L, 0.0);
        assertNotNull(zeroResult);
        assertEquals(0.0, zeroResult.getTargetGrade());
        assertTrue(zeroResult.isAchievable());
        assertEquals(0.0, zeroResult.getRequiredGrade());

        SimulationResult perfectResult = simulateTargetGradeUseCase.simulate(1L, 5.0);
        assertNotNull(perfectResult);
        assertEquals(5.0, perfectResult.getTargetGrade());
        assertFalse(perfectResult.isAchievable());
        assertTrue(perfectResult.getRequiredGrade() > 5.0);

        verify(subjectRepository, times(2)).findById(1L);
    }

    @Test
    @DisplayName("Should calculate current score correctly")
    void shouldCalculateCurrentScoreCorrectly() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        simulateTargetGradeUseCase.simulate(1L, 4.0);

        double expectedCurrentScore = 5.0 * 30.0 + 3.5 * 30.0;
        assertEquals(255.0, expectedCurrentScore, 0.01);

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void shouldHandleDecimalPrecisionCorrectly() {
        Subject subjectWithDecimals = Subject.builder()
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
                    .cutPercentage(33.33)
                    .grade(4.67)
                    .build(),
                EvaluationCut.builder()
                    .id(2L)
                    .cutName("Corte 2")
                    .cutPercentage(33.33)
                    .grade(3.33)
                    .build(),
                EvaluationCut.builder()
                    .id(3L)
                    .cutName("Corte 3")
                    .cutPercentage(33.34)
                    .grade(null)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithDecimals));

        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 4.0);

        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertEquals(33.34, result.getPendingPercentage(), 0.01);
        assertTrue(result.isAchievable());

        verify(subjectRepository).findById(1L);
    }
}
