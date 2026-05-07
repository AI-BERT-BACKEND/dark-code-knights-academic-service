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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateGradeUseCase Tests")
class UpdateGradeUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private UpdateGradeUseCaseImpl updateGradeUseCase;

    private Subject subject;
    private Grade existingGrade;
    private Grade updatedGrade;
    private List<EvaluationCut> evaluationCuts;

    @BeforeEach
    void setUp() {
        evaluationCuts = Arrays.asList(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(60.0)
                .grade(3.5)
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

        existingGrade = Grade.builder()
            .id(1L)
            .cutId(1L)
            .activityName("Parcial 1")
            .gradeValue(4.5)
            .percentage(60.0)
            .build();

        updatedGrade = Grade.builder()
            .activityName("Parcial 1 Actualizado")
            .gradeValue(4.8)
            .percentage(60.0)
            .build();
    }

    @Test
    @DisplayName("Should update grade successfully with valid data")
    void shouldUpdateGradeSuccessfullyWithValidData() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1 Actualizado")
                .gradeValue(4.8)
                .percentage(60.0)
                .build()
        );

        Grade result = updateGradeUseCase.update(1L, 1L, 1L, updatedGrade);

        assertNotNull(result);
        assertEquals("Parcial 1 Actualizado", result.getActivityName());
        assertEquals(4.8, result.getGradeValue());
        assertEquals(60.0, result.getPercentage());
        assertEquals(1L, result.getCutId());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(averageCalculator).recalculateCutAverage(subject, 1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not exist")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findById(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when cut does not exist in subject")
    void shouldThrowSubjectNotFoundExceptionWhenCutDoesNotExistInSubject() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 999L, 1L, updatedGrade)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findById(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeNotFoundException when grade does not exist")
    void shouldThrowGradeNotFoundExceptionWhenGradeDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.empty());

        GradeNotFoundException exception = assertThrows(
            GradeNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeNotFoundException when grade belongs to different cut")
    void shouldThrowGradeNotFoundExceptionWhenGradeBelongsToDifferentCut() {
        Grade gradeFromDifferentCut = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Paracial 1")
            .gradeValue(4.5)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(gradeFromDifferentCut));

        GradeNotFoundException exception = assertThrows(
            GradeNotFoundException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, updatedGrade)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade is negative")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeIsNegative() {
        Grade invalidGrade = Grade.builder()
            .activityName("Parcial Invalido")
            .gradeValue(-1.0)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));

        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, invalidGrade)
        );

        assertTrue(exception.getMessage().contains("-1.0"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade exceeds maximum")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeExceedsMaximum() {
        Grade invalidGrade = Grade.builder()
            .activityName("Paracial Invalido")
            .gradeValue(6.0)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));

        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, invalidGrade)
        );

        assertTrue(exception.getMessage().contains("6.0"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw CutCapacityExceededException when new percentage exceeds capacity")
    void shouldThrowCutCapacityExceededExceptionWhenNewPercentageExceedsCapacity() {
        List<Grade> otherGrades = Arrays.asList(
            Grade.builder()
                .id(2L)
                .cutId(1L)
                .activityName("Quiz 1")
                .gradeValue(3.0)
                .percentage(50.0)
                .build()
        );

        Grade gradeWithIncreasedPercentage = Grade.builder()
            .activityName("Parcial 1")
            .gradeValue(4.5)
            .percentage(70.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.findByCutId(1L)).thenReturn(Arrays.asList(existingGrade, otherGrades.get(0)));

        CutCapacityExceededException exception = assertThrows(
            CutCapacityExceededException.class,
            () -> updateGradeUseCase.update(1L, 1L, 1L, gradeWithIncreasedPercentage)
        );

        assertTrue(exception.getMessage().contains("120.0"));
        assertTrue(exception.getMessage().contains("máximo 100%"));

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should update grade successfully when percentage change is within capacity")
    void shouldUpdateGradeSuccessfullyWhenPercentageChangeIsWithinCapacity() {
        List<Grade> otherGrades = Arrays.asList(
            Grade.builder()
                .id(2L)
                .cutId(1L)
                .activityName("Quiz 1")
                .gradeValue(3.0)
                .percentage(30.0)
                .build()
        );

        Grade gradeWithIncreasedPercentage = Grade.builder()
            .activityName("Parcial 1")
            .gradeValue(4.8)
            .percentage(70.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.findByCutId(1L)).thenReturn(Arrays.asList(existingGrade, otherGrades.get(0)));
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.8)
                .percentage(70.0)
                .build()
        );

        Grade result = updateGradeUseCase.update(1L, 1L, 1L, gradeWithIncreasedPercentage);

        assertNotNull(result);
        assertEquals(4.8, result.getGradeValue());
        assertEquals(70.0, result.getPercentage());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(averageCalculator).recalculateCutAverage(subject, 1L);
    }

    @Test
    @DisplayName("Should update grade successfully when percentage does not change")
    void shouldUpdateGradeSuccessfullyWhenPercentageDoesNotChange() {
        Grade gradeWithSamePercentage = Grade.builder()
            .activityName("Parcial 1 Actualizado")
            .gradeValue(4.8)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1 Actualizado")
                .gradeValue(4.8)
                .percentage(60.0)
                .build()
        );

        Grade result = updateGradeUseCase.update(1L, 1L, 1L, gradeWithSamePercentage);

        assertNotNull(result);
        assertEquals("Parcial 1 Actualizado", result.getActivityName());
        assertEquals(4.8, result.getGradeValue());
        assertEquals(60.0, result.getPercentage());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
        verify(gradeRepository).save(any(Grade.class));
        verify(averageCalculator).recalculateCutAverage(subject, 1L);
    }

    @Test
    @DisplayName("Should handle boundary grade values correctly")
    void shouldHandleBoundaryGradeValuesCorrectly() {
        Grade minGrade = Grade.builder()
            .activityName("Taller Mínimo")
            .gradeValue(0.0)
            .percentage(60.0)
            .build();

        Grade maxGrade = Grade.builder()
            .activityName("Examen Máximo")
            .gradeValue(5.0)
            .percentage(60.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(minGrade);

        Grade minResult = updateGradeUseCase.update(1L, 1L, 1L, minGrade);
        assertNotNull(minResult);
        assertEquals(0.0, minResult.getGradeValue());

        when(gradeRepository.save(any(Grade.class))).thenReturn(maxGrade);
        Grade maxResult = updateGradeUseCase.update(1L, 1L, 1L, maxGrade);
        assertNotNull(maxResult);
        assertEquals(5.0, maxResult.getGradeValue());

        verify(subjectRepository, times(2)).findById(1L);
        verify(gradeRepository, times(2)).findById(1L);
        verify(gradeRepository, times(2)).save(any(Grade.class));
        verify(averageCalculator, times(2)).recalculateCutAverage(subject, 1L);
    }
}
