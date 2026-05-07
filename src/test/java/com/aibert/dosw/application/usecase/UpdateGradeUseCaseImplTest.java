package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.CutCapacityExceededException;
import com.aibert.dosw.domain.exceptions.GradeNotFoundException;
import com.aibert.dosw.domain.exceptions.GradeOutOfRangeException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.GradeRepositoryPort;
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
@DisplayName("UpdateGradeUseCaseImpl Tests")
class UpdateGradeUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private UpdateGradeUseCaseImpl updateGradeUseCase;

    private Subject testSubject;
    private EvaluationCut testCut;
    private Grade existingGrade;
    private Grade updatedGrade;

    @BeforeEach
    void setUp() {
        testCut = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(30.0)
            .grade(null)
            .build();

        testSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(testCut))
            .build();

        existingGrade = Grade.builder()
            .id(1L)
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(4.0)
            .percentage(20.0)
            .build();

        updatedGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1 Updated")
            .gradeValue(4.5)
            .percentage(25.0)
            .build();
    }

    @Test
    @DisplayName("Should update grade successfully with valid data")
    void shouldUpdateGradeSuccessfully() {
        // Given
        Grade savedGrade = Grade.builder()
            .id(1L)
            .cutId(1L)
            .activityName("Exam 1 Updated")
            .gradeValue(4.5)
            .percentage(25.0)
            .build();
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(savedGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = updateGradeUseCase.update(1L, 1L, 1L, updatedGrade);

        // Then
        assertNotNull(result);
        assertEquals("Exam 1 Updated", result.getActivityName());
        assertEquals(4.5, result.getGradeValue());
        assertEquals(25.0, result.getPercentage());
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCutId());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findByCutId(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findById(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeNotFoundException when grade not found")
    void shouldThrowGradeNotFoundExceptionWhenGradeNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        GradeNotFoundException exception = assertThrows(
            GradeNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeNotFoundException when grade belongs to different cut")
    void shouldThrowGradeNotFoundExceptionWhenGradeBelongsToDifferentCut() {
        // Given
        Grade gradeFromDifferentCut = new Grade(
            existingGrade.getId(),
            2L,
            existingGrade.getActivityName(),
            existingGrade.getGradeValue(),
            existingGrade.getPercentage()
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(gradeFromDifferentCut));

        // When & Then
        GradeNotFoundException exception = assertThrows(
            GradeNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade value is negative")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeValueIsNegative() {
        // Given
        Grade negativeGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            -1.0,
            updatedGrade.getPercentage()
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));

        // When & Then
        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, negativeGrade)
        );
        
        assertTrue(exception.getMessage().contains("-1.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade value exceeds maximum")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeValueExceedsMaximum() {
        // Given
        Grade highGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            5.1,
            updatedGrade.getPercentage()
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));

        // When & Then
        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, highGrade)
        );
        
        assertTrue(exception.getMessage().contains("5.1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw CutCapacityExceededException when new percentage causes capacity exceeded")
    void shouldThrowCutCapacityExceededExceptionWhenNewPercentageCausesCapacityExceeded() {
        // Given
        Grade otherGrade = Grade.builder()
            .id(2L)
            .cutId(1L)
            .activityName("Other Exam")
            .gradeValue(3.5)
            .percentage(80.0)
            .build();
        
        Grade highPercentageGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            updatedGrade.getGradeValue(),
            30.0
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of(existingGrade, otherGrade));

        // When & Then
        CutCapacityExceededException exception = assertThrows(
            CutCapacityExceededException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, highPercentageGrade)
        );
        
        assertNotNull(exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findByCutId(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should update successfully when percentage does not change")
    void shouldUpdateSuccessfullyWhenPercentageDoesNotChange() {
        // Given
        Grade samePercentageGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            updatedGrade.getGradeValue(),
            20.0
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(samePercentageGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = updateGradeUseCase.update(1L, 1L, 1L, samePercentageGrade);

        // Then
        assertNotNull(result);
        assertEquals(20.0, result.getPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should update successfully when percentage changes but capacity is not exceeded")
    void shouldUpdateSuccessfullyWhenPercentageChangesButCapacityIsNotExceeded() {
        // Given
        Grade otherGrade = Grade.builder()
            .id(2L)
            .cutId(1L)
            .activityName("Other Exam")
            .gradeValue(3.5)
            .percentage(50.0)
            .build();
        
        Grade validPercentageGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            updatedGrade.getGradeValue(),
            40.0
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of(existingGrade, otherGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(validPercentageGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = updateGradeUseCase.update(1L, 1L, 1L, validPercentageGrade);

        // Then
        assertNotNull(result);
        assertEquals(40.0, result.getPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findByCutId(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should update successfully at boundary value 0.0")
    void shouldUpdateSuccessfullyAtBoundaryValue0() {
        // Given
        Grade zeroGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            0.0,
            20.0
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(zeroGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = updateGradeUseCase.update(1L, 1L, 1L, zeroGrade);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getGradeValue());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should update successfully at boundary value 5.0")
    void shouldUpdateSuccessfullyAtBoundaryValue5() {
        // Given
        Grade maxGrade = new Grade(
            updatedGrade.getId(),
            updatedGrade.getCutId(),
            updatedGrade.getActivityName(),
            5.0,
            20.0
        );
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(maxGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = updateGradeUseCase.update(1L, 1L, 1L, maxGrade);

        // Then
        assertNotNull(result);
        assertEquals(5.0, result.getGradeValue());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when cut not found in subject")
    void shouldThrowSubjectNotFoundExceptionWhenCutNotFoundInSubject() {
        // Given
        Long nonExistentCutId = 999L;
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> updateGradeUseCase.update(1L, nonExistentCutId, 1L, updatedGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findById(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }
}
