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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterGradeUseCaseImpl Tests")
class RegisterGradeUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private RegisterGradeUseCaseImpl registerGradeUseCase;

    private Subject testSubject;
    private EvaluationCut testCut;
    private Grade testGrade;

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

        testGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(4.5)
            .percentage(20.0)
            .build();
    }

    @Test
    @DisplayName("Should register grade successfully with valid data")
    void shouldRegisterGradeSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of());
        when(gradeRepository.save(any(Grade.class))).thenReturn(testGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = registerGradeUseCase.register(1L, 1L, testGrade);

        // Then
        assertNotNull(result);
        assertEquals("Exam 1", result.getActivityName());
        assertEquals(4.5, result.getGradeValue());
        assertEquals(20.0, result.getPercentage());
        assertEquals(1L, result.getCutId());
        verify(subjectRepository, times(1)).findById(1L);
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
            () -> registerGradeUseCase.register(1L, 1L, testGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
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
            () -> registerGradeUseCase.register(1L, nonExistentCutId, testGrade)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade value is negative")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeValueIsNegative() {
        // Given
        Grade negativeGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(-1.0)
            .percentage(20.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> registerGradeUseCase.register(1L, 1L, negativeGrade)
        );
        
        assertTrue(exception.getMessage().contains("-1.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw GradeOutOfRangeException when grade value exceeds maximum")
    void shouldThrowGradeOutOfRangeExceptionWhenGradeValueExceedsMaximum() {
        // Given
        Grade highGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(5.1)
            .percentage(20.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        GradeOutOfRangeException exception = assertThrows(
            GradeOutOfRangeException.class,
            () -> registerGradeUseCase.register(1L, 1L, highGrade)
        );
        
        assertTrue(exception.getMessage().contains("5.1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should throw CutCapacityExceededException when cut capacity exceeded")
    void shouldThrowCutCapacityExceededExceptionWhenCutCapacityExceeded() {
        // Given
        Grade existingGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(4.0)
            .percentage(90.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of(existingGrade));

        // When & Then
        CutCapacityExceededException exception = assertThrows(
            CutCapacityExceededException.class,
            () -> registerGradeUseCase.register(1L, 1L, testGrade)
        );
        
        assertNotNull(exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findByCutId(1L);
        verify(gradeRepository, never()).save(any(Grade.class));
        verify(averageCalculator, never()).recalculateCutAverage(any(), any());
    }

    @Test
    @DisplayName("Should register grade successfully at boundary value 0.0")
    void shouldRegisterGradeSuccessfullyAtBoundaryValue0() {
        // Given
        Grade zeroGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(0.0)
            .percentage(20.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of());
        when(gradeRepository.save(any(Grade.class))).thenReturn(zeroGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = registerGradeUseCase.register(1L, 1L, zeroGrade);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getGradeValue());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should register grade successfully at boundary value 5.0")
    void shouldRegisterGradeSuccessfullyAtBoundaryValue5() {
        // Given
        Grade maxGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(5.0)
            .percentage(20.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of());
        when(gradeRepository.save(any(Grade.class))).thenReturn(maxGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = registerGradeUseCase.register(1L, 1L, maxGrade);

        // Then
        assertNotNull(result);
        assertEquals(5.0, result.getGradeValue());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should register grade successfully with percentage at 0")
    void shouldRegisterGradeSuccessfullyWithPercentageAt0() {
        // Given
        Grade zeroPercentageGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(4.0)
            .percentage(0.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of());
        when(gradeRepository.save(any(Grade.class))).thenReturn(zeroPercentageGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = registerGradeUseCase.register(1L, 1L, zeroPercentageGrade);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should register grade successfully with percentage at 100")
    void shouldRegisterGradeSuccessfullyWithPercentageAt100() {
        // Given
        Grade fullPercentageGrade = Grade.builder()
            .cutId(1L)
            .activityName("Exam 1")
            .gradeValue(4.0)
            .percentage(100.0)
            .build();
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(List.of());
        when(gradeRepository.save(any(Grade.class))).thenReturn(fullPercentageGrade);
        doNothing().when(averageCalculator).recalculateCutAverage(testSubject, 1L);

        // When
        Grade result = registerGradeUseCase.register(1L, 1L, fullPercentageGrade);

        // Then
        assertNotNull(result);
        assertEquals(100.0, result.getPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any(Grade.class));
        verify(averageCalculator, times(1)).recalculateCutAverage(testSubject, 1L);
    }

    @Test
    @DisplayName("Should get grades by cut successfully")
    void shouldGetGradesByCutSuccessfully() {
        // Given
        List<Grade> existingGrades = List.of(testGrade);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.findByCutId(1L)).thenReturn(existingGrades);

        // When
        List<Grade> result = registerGradeUseCase.getGradesByCut(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Exam 1", result.get(0).getActivityName());
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).findByCutId(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting grades for non-existent subject")
    void shouldThrowSubjectNotFoundExceptionWhenGettingGradesForNonExistentSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.getGradesByCut(1L, 1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting grades for non-existent cut")
    void shouldThrowSubjectNotFoundExceptionWhenGettingGradesForNonExistentCut() {
        // Given
        Long nonExistentCutId = 999L;
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> registerGradeUseCase.getGradesByCut(1L, nonExistentCutId)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).findByCutId(any());
    }
}
