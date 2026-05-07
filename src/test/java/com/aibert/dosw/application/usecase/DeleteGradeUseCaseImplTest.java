package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.GradeNotFoundException;
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
@DisplayName("DeleteGradeUseCaseImpl Tests")
class DeleteGradeUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private DeleteGradeUseCaseImpl deleteGradeUseCase;

    private Subject testSubject;
    private EvaluationCut testCut;
    private Grade existingGrade;

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
    }

    @Test
    @DisplayName("Should delete grade successfully with valid data")
    void shouldDeleteGradeSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        doNothing().when(gradeRepository).deleteById(1L);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        assertDoesNotThrow(() -> deleteGradeUseCase.delete(1L, 1L, 1L));

        // Then
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).deleteById(1L);
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
            () -> deleteGradeUseCase.delete(1L, 1L, 1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findById(any());
        verify(gradeRepository, never()).deleteById(any());
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
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
            () -> deleteGradeUseCase.delete(1L, nonExistentCutId, 1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findById(any());
        verify(gradeRepository, never()).deleteById(any());
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
            () -> deleteGradeUseCase.delete(1L, 1L, 1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).deleteById(any());
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
            () -> deleteGradeUseCase.delete(1L, 1L, 1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).deleteById(any());
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should delete grade successfully with any valid IDs")
    void shouldDeleteGradeSuccessfullyWithAnyValidIds() {
        // Given
        Long subjectId = 999L;
        Long cutId = 888L;
        Long gradeId = 777L;
        
        Subject testSubject = Subject.builder()
            .id(subjectId)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of(
                EvaluationCut.builder()
                    .id(cutId)
                    .cutName("Corte 1")
                    .cutPercentage(30.0)
                    .grade(null)
                    .build()
            ))
            .build();
            
        Grade testGrade = Grade.builder()
            .id(gradeId)
            .cutId(cutId)
            .activityName("Exam 1")
            .gradeValue(4.0)
            .percentage(20.0)
            .build();
        
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(testGrade));
        doNothing().when(gradeRepository).deleteById(gradeId);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, cutId);

        // When
        assertDoesNotThrow(() -> deleteGradeUseCase.delete(subjectId, cutId, gradeId));

        // Then
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(gradeRepository, times(1)).findById(gradeId);
        verify(gradeRepository, times(1)).deleteById(gradeId);
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, cutId);
    }

    @Test
    @DisplayName("Should call deleteById exactly once when deletion is successful")
    void shouldCallDeleteByIdExactlyOnceWhenDeletionIsSuccessful() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        doNothing().when(gradeRepository).deleteById(1L);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        deleteGradeUseCase.delete(1L, 1L, 1L);

        // Then
        verify(gradeRepository, times(1)).deleteById(1L);
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should not call deleteById when grade not found")
    void shouldNotCallDeleteByIdWhenGradeNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GradeNotFoundException.class, () -> deleteGradeUseCase.delete(1L, 1L, 1L));

        // Then
        verify(gradeRepository, never()).deleteById(any());
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should not call deleteById when grade belongs to different cut")
    void shouldNotCallDeleteByIdWhenGradeBelongsToDifferentCut() {
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
        assertThrows(GradeNotFoundException.class, () -> deleteGradeUseCase.delete(1L, 1L, 1L));

        // Then
        verify(gradeRepository, never()).deleteById(any());
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should not call recalculateCutAverage when subject not found")
    void shouldNotCallRecalculateCutAverageWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SubjectNotFoundException.class, () -> deleteGradeUseCase.delete(1L, 1L, 1L));

        // Then
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should not call recalculateCutAverage when cut not found")
    void shouldNotCallRecalculateCutAverageWhenCutNotFound() {
        // Given
        Long nonExistentCutId = 999L;
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        assertThrows(SubjectNotFoundException.class, () -> deleteGradeUseCase.delete(1L, nonExistentCutId, 1L));

        // Then
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should not call recalculateCutAverage when grade not found")
    void shouldNotCallRecalculateCutAverageWhenGradeNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GradeNotFoundException.class, () -> deleteGradeUseCase.delete(1L, 1L, 1L));

        // Then
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }
}
