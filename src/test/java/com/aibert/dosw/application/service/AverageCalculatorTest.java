package com.aibert.dosw.application.service;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AverageCalculator Tests")
class AverageCalculatorTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private GradeRepositoryPort gradeRepository;

    @InjectMocks
    private AverageCalculator averageCalculator;

    private Subject subject;
    private List<Grade> grades;
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

        grades = Arrays.asList(
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
    }

    @Test
    @DisplayName("Should recalculate cut average with valid grades")
    void shouldRecalculateCutAverageWithValidGrades() {
        when(gradeRepository.findByCutId(1L)).thenReturn(grades);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(gradeRepository).findByCutId(1L);
        verify(subjectRepository).save(any(Subject.class));
        
        double expectedAverage = (4.5 * 60.0 + 3.0 * 40.0) / (60.0 + 40.0);
        assertEquals(3.9, expectedAverage, 0.01);
    }

    @Test
    @DisplayName("Should set null average when no grades exist")
    void shouldSetNullAverageWhenNoGradesExist() {
        when(gradeRepository.findByCutId(1L)).thenReturn(Collections.emptyList());
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(gradeRepository).findByCutId(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should handle zero total percentage")
    void shouldHandleZeroTotalPercentage() {
        List<Grade> zeroPercentageGrades = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(0.0)
                .build()
        );
        
        when(gradeRepository.findByCutId(1L)).thenReturn(zeroPercentageGrades);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(gradeRepository).findByCutId(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should preserve other cuts when updating specific cut")
    void shouldPreserveOtherCutsWhenUpdatingSpecificCut() {
        when(gradeRepository.findByCutId(1L)).thenReturn(grades);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(subjectRepository).save(argThat(savedSubject -> {
            List<EvaluationCut> savedCuts = savedSubject.getEvaluationCuts();
            EvaluationCut unchangedCut = savedCuts.stream()
                .filter(c -> c.getId().equals(2L))
                .findFirst()
                .orElse(null);
            
            return unchangedCut != null && 
                   unchangedCut.getCutName().equals("Corte 2") &&
                   unchangedCut.getCutPercentage().equals(60.0) &&
                   unchangedCut.getGrade().equals(3.5);
        }));
    }

    @Test
    @DisplayName("Should handle single grade")
    void shouldHandleSingleGrade() {
        List<Grade> singleGrade = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(100.0)
                .build()
        );
        
        when(gradeRepository.findByCutId(1L)).thenReturn(singleGrade);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(gradeRepository).findByCutId(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void shouldHandleDecimalPrecisionCorrectly() {
        List<Grade> decimalGrades = Arrays.asList(
            Grade.builder()
                .id(1L)
                .cutId(1L)
                .activityName("Parcial 1")
                .gradeValue(3.333)
                .percentage(33.33)
                .build(),
            Grade.builder()
                .id(2L)
                .cutId(1L)
                .activityName("Quiz 1")
                .gradeValue(4.667)
                .percentage(66.67)
                .build()
        );
        
        when(gradeRepository.findByCutId(1L)).thenReturn(decimalGrades);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        averageCalculator.recalculateCutAverage(subject, 1L);

        verify(gradeRepository).findByCutId(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    private void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
