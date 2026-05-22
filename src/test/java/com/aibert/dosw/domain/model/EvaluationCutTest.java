package com.aibert.dosw.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvaluationCut Tests")
class EvaluationCutTest {

    @Test
    @DisplayName("Should create EvaluationCut using builder")
    void shouldCreateEvaluationCutUsingBuilder() {
        EvaluationCut cut = EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .grade(3.5)
                .build();

        assertEquals(1L, cut.getId());
        assertEquals("Corte 1", cut.getCutName());
        assertEquals(40.0, cut.getCutPercentage());
        assertEquals(3.5, cut.getGrade());
    }

    @Test
    @DisplayName("Should create EvaluationCut using no-args constructor")
    void shouldCreateEvaluationCutUsingNoArgsConstructor() {
        EvaluationCut cut = new EvaluationCut();

        assertNotNull(cut);
        assertNull(cut.getId());
        assertNull(cut.getCutName());
        assertNull(cut.getCutPercentage());
        assertNull(cut.getGrade());
    }

    @Test
    @DisplayName("Should create EvaluationCut using all-args constructor")
    void shouldCreateEvaluationCutUsingAllArgsConstructor() {
        EvaluationCut cut = new EvaluationCut(2L, "Corte 2", 60.0, 4.0);

        assertEquals(2L, cut.getId());
        assertEquals("Corte 2", cut.getCutName());
        assertEquals(60.0, cut.getCutPercentage());
        assertEquals(4.0, cut.getGrade());
    }

    @Test
    @DisplayName("Should handle null grade (pending cut)")
    void shouldHandleNullGrade() {
        EvaluationCut cut = EvaluationCut.builder()
                .id(1L)
                .cutName("Pendiente")
                .cutPercentage(30.0)
                .grade(null)
                .build();

        assertNull(cut.getGrade());
    }

    @Test
    @DisplayName("Should handle boundary percentage values")
    void shouldHandleBoundaryPercentageValues() {
        EvaluationCut single = EvaluationCut.builder()
                .id(1L).cutName("Único").cutPercentage(100.0).grade(null).build();

        assertEquals(100.0, single.getCutPercentage());
    }
}
