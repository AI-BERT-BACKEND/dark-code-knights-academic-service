package com.aibert.dosw.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Grade Tests")
class GradeTest {

    @Test
    @DisplayName("Should create Grade using builder")
    void shouldCreateGradeUsingBuilder() {
        Grade grade = Grade.builder()
                .id(1L)
                .cutId(2L)
                .activityName("Parcial 1")
                .gradeValue(4.5)
                .percentage(60.0)
                .build();

        assertEquals(1L, grade.getId());
        assertEquals(2L, grade.getCutId());
        assertEquals("Parcial 1", grade.getActivityName());
        assertEquals(4.5, grade.getGradeValue());
        assertEquals(60.0, grade.getPercentage());
    }

    @Test
    @DisplayName("Should create Grade using no-args constructor")
    void shouldCreateGradeUsingNoArgsConstructor() {
        Grade grade = new Grade();

        assertNotNull(grade);
        assertNull(grade.getId());
        assertNull(grade.getCutId());
        assertNull(grade.getActivityName());
        assertNull(grade.getGradeValue());
        assertNull(grade.getPercentage());
    }

    @Test
    @DisplayName("Should create Grade using all-args constructor")
    void shouldCreateGradeUsingAllArgsConstructor() {
        Grade grade = new Grade(1L, 2L, "Quiz", 3.0, 40.0);

        assertEquals(1L, grade.getId());
        assertEquals(2L, grade.getCutId());
        assertEquals("Quiz", grade.getActivityName());
        assertEquals(3.0, grade.getGradeValue());
        assertEquals(40.0, grade.getPercentage());
    }

    @Test
    @DisplayName("Should handle minimum grade value 0.0")
    void shouldHandleMinimumGradeValue() {
        Grade grade = Grade.builder()
                .id(1L).cutId(1L).activityName("Taller").gradeValue(0.0).percentage(20.0).build();

        assertEquals(0.0, grade.getGradeValue());
    }

    @Test
    @DisplayName("Should handle maximum grade value 5.0")
    void shouldHandleMaximumGradeValue() {
        Grade grade = Grade.builder()
                .id(1L).cutId(1L).activityName("Examen").gradeValue(5.0).percentage(100.0).build();

        assertEquals(5.0, grade.getGradeValue());
    }
}
