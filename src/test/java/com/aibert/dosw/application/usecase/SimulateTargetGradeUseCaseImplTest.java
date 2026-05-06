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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulateTargetGradeUseCaseImpl Tests")
class SimulateTargetGradeUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private SimulateTargetGradeUseCaseImpl simulateTargetGradeUseCase;

    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of())
            .build();
    }

    @Test
    @DisplayName("Should simulate target grade successfully with achievable grade")
    void shouldSimulateTargetGradeSuccessfullyWithAchievableGrade() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(30.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 4.0);

        // Then
        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 4.0 * 30.0 = 120.0
        // Target score: 4.0 * 100.0 = 400.0
        // Required: (400.0 - 120.0) / 70.0 = 4.0
        assertEquals(4.0, result.getRequiredGrade());
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade successfully with unachievable grade")
    void shouldSimulateTargetGradeSuccessfullyWithUnachievableGrade() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(70.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 5.0);

        // Then
        assertNotNull(result);
        assertEquals(5.0, result.getTargetGrade());
        assertFalse(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 4.0 * 30.0 = 120.0
        // Target score: 5.0 * 100.0 = 500.0
        // Required: (500.0 - 120.0) / 70.0 = 5.428... (unachievable)
        assertTrue(result.getRequiredGrade() > 5.0);
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade successfully with minimum required grade 0.0")
    void shouldSimulateTargetGradeSuccessfullyWithMinimumRequiredGrade0() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(5.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(70.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 3.5);

        // Then
        assertNotNull(result);
        assertEquals(3.5, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 5.0 * 30.0 = 150.0
        // Target score: 3.5 * 100.0 = 350.0
        // Required: (350.0 - 150.0) / 70.0 = 2.857...
        assertEquals(2.857142857142857, result.getRequiredGrade()); // Math.max(0.0, 2.857...)
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> simulateTargetGradeUseCase.simulate(1L, 4.0)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw NoPendingCutsException when all cuts have grades")
    void shouldThrowNoPendingCutsExceptionWhenAllCutsHaveGrades() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(3.5)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(30.0)
                .grade(4.5)
                .build()
        );
        
        Subject subjectWithAllGrades = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithAllGrades));

        // When & Then
        NoPendingCutsException exception = assertThrows(
            NoPendingCutsException.class,
            () -> simulateTargetGradeUseCase.simulate(1L, 4.0)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade with single pending cut")
    void shouldSimulateTargetGradeWithSinglePendingCut() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(70.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 4.0);

        // Then
        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 4.0 * 30.0 = 120.0
        // Target score: 4.0 * 100.0 = 400.0
        // Required: (400.0 - 120.0) / 70.0 = 4.0
        assertEquals(4.0, result.getRequiredGrade());
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade with all cuts pending")
    void shouldSimulateTargetGradeWithAllCutsPending() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(30.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithAllPending = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithAllPending));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 4.0);

        // Then
        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(100.0, result.getPendingPercentage());
        
        // Current score: 0.0
        // Target score: 4.0 * 100.0 = 400.0
        // Required: (400.0 - 0.0) / 100.0 = 4.0
        assertEquals(4.0, result.getRequiredGrade());
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade with boundary target grade 5.0")
    void shouldSimulateTargetGradeWithBoundaryTargetGrade5() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(70.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 5.0);

        // Then
        assertNotNull(result);
        assertEquals(5.0, result.getTargetGrade());
        assertFalse(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 4.0 * 30.0 = 120.0
        // Target score: 5.0 * 100.0 = 500.0
        // Required: (500.0 - 120.0) / 70.0 = 5.428... (unachievable)
        assertTrue(result.getRequiredGrade() > 5.0);
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade with boundary target grade 0.0")
    void shouldSimulateTargetGradeWithBoundaryTargetGrade0() {
        // Given
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(70.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            cuts
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithCuts));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(1L, 0.0);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(70.0, result.getPendingPercentage());
        
        // Current score: 4.0 * 30.0 = 120.0
        // Target score: 0.0 * 100.0 = 0.0
        // Required: (0.0 - 120.0) / 70.0 = -1.714...
        assertEquals(0.0, result.getRequiredGrade()); // Math.max(0.0, -1.714...)
        
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should simulate target grade with any valid subject ID")
    void shouldSimulateTargetGradeWithAnyValidSubjectId() {
        // Given
        Long subjectId = 999L;
        List<EvaluationCut> cuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(50.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(50.0)
                .grade(null)
                .build()
        );
        
        Subject testSubject = Subject.builder()
            .id(subjectId)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(cuts)
            .build();
        
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));

        // When
        SimulationResult result = simulateTargetGradeUseCase.simulate(subjectId, 4.0);

        // Then
        assertNotNull(result);
        assertEquals(4.0, result.getTargetGrade());
        assertTrue(result.isAchievable());
        assertEquals(50.0, result.getPendingPercentage());
        
        verify(subjectRepository, times(1)).findById(subjectId);
    }
}
