package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.CutCapacityExceededException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterGradeUseCase Tests")
class RegisterGradeUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private RegisterGradeUseCaseImpl registerGradeUseCase;

    private Subject subject;
    private Grade validGrade;
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

        validGrade = Grade.builder()
            .activityName("Parcial 1")
            .gradeValue(4.5)
            .percentage(60.0)
            .build();
    }

    @Test
    @DisplayName("Should register grade successfully with valid data")
    void shouldRegisterGradeSuccessfullyWithValidData() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(1L)).thenReturn(Collections.emptyList());
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(60.0)
                .build()
        );

        Grade result = registerGradeUseCase.register(1L, 1L, validGrade);

        assertNotNull(result);
        assertEquals("Parcial 1", result.getActivityName());
        assertEquals(4.5, result.getGradeValue());
        assertEquals(60.0, result.getPercentage());
        assertEquals(1L, result.getCutId());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(averageCalculator).recalculateCutAverage(subject, 1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not exist")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.register(1L, 1L, validGrade)
        );

        assertTrue(exception.getMessage().contains("1"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when cut does not exist in subject")
    void shouldThrowSubjectNotFoundExceptionWhenCutDoesNotExistInSubject() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.register(1L, 999L, validGrade)
        );

        assertTrue(exception.getMessage().contains("1"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade is negative")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeIsNegative() {
        Grade invalidGrade = Grade.builder()
            .activityName("Paracial Invalido")
            .gradeValue(-1.0)
            .percentage(50.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> registerGradeUseCase.register(1L, 1L, invalidGrade)
        );

        assertTrue(exception.getMessage().contains("-1.0"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade exceeds maximum")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeExceedsMaximum() {
        Grade invalidGrade = Grade.builder()
            .activityName("Paracial Invalido")
            .gradeValue(6.0)
            .percentage(50.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> registerGradeUseCase.register(1L, 1L, invalidGrade)
        );

        assertTrue(exception.getMessage().contains("6.0"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should throw CutCapacityExceededException when cut capacity is exceeded")
    void shouldThrowCutCapacityExceededExceptionWhenCutCapacityIsExceeded() {
        List<Grade> existingGrades = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(80.0)
                .build()
        );

        Grade newGrade = Grade.builder()
            .activityName("Quiz 1")
            .gradeValue(3.0)
            .percentage(30.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(1L)).thenReturn(existingGrades);

        CutCapacityExceededException exception = assertThrows(
            CutCapacityExceededException.class,
            () -> registerGradeUseCase.register(1L, 1L, newGrade)
        );

        assertTrue(exception.getMessage().contains("110.0"));
        assertTrue(exception.getMessage().contains("máximo 100%"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), anyLong());
    }

    @Test
    @DisplayName("Should register grade when cut capacity allows exactly 100%")
    void shouldRegisterGradeWhenCutCapacityAllowsExactly100Percent() {
        List<Grade> existingGrades = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(70.0)
                .build()
        );

        Grade newGrade = Grade.builder()
            .activityName("Quiz 1")
            .gradeValue(3.0)
            .percentage(30.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(1L)).thenReturn(existingGrades);
        when(gradeRepository.save(any(Grade.class))).thenReturn(
            Grade.builder()
                .id(2L)
                .cutId(1L)
                .activityName("Quiz 1")
                .gradeValue(3.0)
                .percentage(30.0)
                .build()
        );

        Grade result = registerGradeUseCase.register(1L, 1L, newGrade);

        assertNotNull(result);
        assertEquals("Quiz 1", result.getActivityName());
        assertEquals(3.0, result.getGradeValue());
        assertEquals(30.0, result.getPercentage());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(averageCalculator).recalculateCutAverage(subject, 1L);
    }

    @Test
    @DisplayName("Should get grades by cut successfully")
    void shouldGetGradesByCutSuccessfully() {
        List<Grade> expectedGrades = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(60.0)
                .build(),
            Grade.builder()
                .id(2L)
                .cutId(1L)
                .activityName("Quiz 1")
                .gradeValue(3.0)
                .percentage(40.0)
                .build()
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(1L)).thenReturn(expectedGrades);

        List<Grade> result = registerGradeUseCase.getGradesByCut(1L, 1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Parcial 1", result.get(0).getActivityName());
        assertEquals("Quiz 1", result.get(1).getActivityName());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findByCutId(1L);
    }

    @Test
    @DisplayName("Should return empty list when cut has no grades")
    void shouldReturnEmptyListWhenCutHasNoGrades() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(2L)).thenReturn(Collections.emptyList());

        List<Grade> result = registerGradeUseCase.getGradesByCut(1L, 2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(subjectRepository).findById(1L);
        verify(gradeRepository).findByCutId(2L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting grades for non-existent subject")
    void shouldThrowSubjectNotFoundExceptionWhenGettingGradesForNonExistentSubject() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.getGradesByCut(999L, 1L)
        );

        assertTrue(exception.getMessage().contains("999"));
        
        verify(subjectRepository).findById(999L);
        verify(gradeRepository, never()).findByCutId(anyLong());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting grades for non-existent cut")
    void shouldThrowSubjectNotFoundExceptionWhenGettingGradesForNonExistentCut() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.getGradesByCut(1L, 999L)
        );

        assertTrue(exception.getMessage().contains("1"));
        
        verify(subjectRepository).findById(1L);
        verify(gradeRepository, never()).findByCutId(anyLong());
    }

    @Test
    @DisplayName("Should handle boundary grade values correctly")
    void shouldHandleBoundaryGradeValuesCorrectly() {
        Grade minGrade = Grade.builder()
            .activityName("Taller Mínimo")
            .gradeValue(0.0)
            .percentage(50.0)
            .build();

        Grade maxGrade = Grade.builder()
            .activityName("Examen Máximo")
            .gradeValue(5.0)
            .percentage(50.0)
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(gradeRepository.findByCutId(1L)).thenReturn(Collections.emptyList());
        when(gradeRepository.save(any(Grade.class))).thenReturn(minGrade);

        Grade minResult = registerGradeUseCase.register(1L, 1L, minGrade);
        assertNotNull(minResult);
        assertEquals(0.0, minResult.getGradeValue());

        when(gradeRepository.save(any(Grade.class))).thenReturn(maxGrade);
        Grade maxResult = registerGradeUseCase.register(1L, 1L, maxGrade);
        assertNotNull(maxResult);
        assertEquals(5.0, maxResult.getGradeValue());

        verify(subjectRepository, times(2)).findById(1L);
        verify(gradeRepository, times(2)).save(any(Grade.class));
        verify(averageCalculator, times(2)).recalculateCutAverage(subject, 1L);
    }
}
