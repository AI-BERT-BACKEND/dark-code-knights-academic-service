package com.aibert.dosw.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Subject Tests")
class SubjectTest {

    private Subject subject;
    private EvaluationCut evaluationCut;

    @BeforeEach
    void setUp() {
        evaluationCut = EvaluationCut.builder()
            .id(1L)
            .cutName("Corte 1")
            .cutPercentage(40.0)
            .grade(4.5)
            .build();

        subject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(evaluationCut))
            .build();
    }

    @Test
    @DisplayName("Should create Subject with builder")
    void shouldCreateSubjectWithBuilder() {
        assertNotNull(subject);
        assertEquals(1L, subject.getId());
        assertEquals("student-test", subject.getStudentId());
        assertEquals("Cálculo Diferencial", subject.getSubjectName());
        assertEquals(4, subject.getCredits());
        assertEquals("Prof. Ramírez", subject.getTeacherName());
        assertEquals("2025-1", subject.getSemester());
        assertEquals(1, subject.getEvaluationCuts().size());
    }

    @Test
    @DisplayName("Should create Subject with all args constructor")
    void shouldCreateSubjectWithAllArgsConstructor() {
        Subject newSubject = new Subject(
            2L,
            "student-2",
            "Física General",
            3,
            "Prof. Torres",
            "2025-1",
            null,
                null,
            Collections.emptyList()
        );
        
        assertNotNull(newSubject);
        assertEquals(2L, newSubject.getId());
        assertEquals("student-2", newSubject.getStudentId());
        assertEquals("Física General", newSubject.getSubjectName());
        assertEquals(3, newSubject.getCredits());
        assertEquals("Prof. Torres", newSubject.getTeacherName());
        assertEquals("2025-1", newSubject.getSemester());
        assertTrue(newSubject.getEvaluationCuts().isEmpty());
    }

    @Test
    @DisplayName("Should create Subject with no args constructor")
    void shouldCreateSubjectWithNoArgsConstructor() {
        Subject newSubject = new Subject();
        
        assertNotNull(newSubject);
        assertNull(newSubject.getId());
        assertNull(newSubject.getStudentId());
        assertNull(newSubject.getSubjectName());
        assertNull(newSubject.getCredits());
        assertNull(newSubject.getTeacherName());
        assertNull(newSubject.getSemester());
        assertNull(newSubject.getEvaluationCuts());
    }

    @Test
    @DisplayName("Should use getters correctly")
    void shouldUseGettersCorrectly() {
        Subject newSubject = Subject.builder()
            .id(3L)
            .studentId("student-3")
            .subjectName("Álgebra Lineal")
            .credits(4)
            .teacherName("Prof. García")
            .semester("2024-2")
            .evaluationCuts(Collections.emptyList())
            .build();
        
        assertEquals(3L, newSubject.getId());
        assertEquals("student-3", newSubject.getStudentId());
        assertEquals("Álgebra Lineal", newSubject.getSubjectName());
        assertEquals(4, newSubject.getCredits());
        assertEquals("Prof. García", newSubject.getTeacherName());
        assertEquals("2024-2", newSubject.getSemester());
        assertTrue(newSubject.getEvaluationCuts().isEmpty());
    }

    
    @Test
    @DisplayName("Should use default equals and hashCode behavior")
    void shouldUseDefaultEqualsAndHashCodeBehavior() {
        Subject subject1 = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Física General")
            .credits(3)
            .teacherName("Prof. Torres")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(evaluationCut))
            .build();
        
        Subject subject2 = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Física General")
            .credits(3)
            .teacherName("Prof. Torres")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(evaluationCut))
            .build();
        
        // With default Object behavior, different instances are not equal
        assertNotEquals(subject1, subject2);
        // An object is always equal to itself
        assertEquals(subject1, subject1);
        // Same reference should be equal
        Subject subject1Ref = subject1;
        assertEquals(subject1, subject1Ref);
    }

    @Test
    @DisplayName("Should use default toString behavior")
    void shouldUseDefaultToStringBehavior() {
        String toString = subject.toString();
        
        assertNotNull(toString);
        // Default Object.toString() format: className@hashCode
        assertTrue(toString.contains("Subject@"));
        // Should not contain field-specific information with default toString
        assertFalse(toString.contains("id="));
        assertFalse(toString.contains("subjectName="));
        assertFalse(toString.contains("credits="));
    }

    @Test
    @DisplayName("Should handle subject with no evaluation cuts")
    void shouldHandleSubjectWithNoEvaluationCuts() {
        Subject subjectWithoutCuts = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(2)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();

        assertNotNull(subjectWithoutCuts);
        assertTrue(subjectWithoutCuts.getEvaluationCuts().isEmpty());
    }

    @Test
    @DisplayName("Should handle subject with single evaluation cut")
    void shouldHandleSubjectWithSingleEvaluationCut() {
        Subject subjectWithSingleCut = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(2)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(evaluationCut))
            .build();

        assertNotNull(subjectWithSingleCut);
        assertEquals(1, subjectWithSingleCut.getEvaluationCuts().size());
        assertEquals("Corte 1", subjectWithSingleCut.getEvaluationCuts().get(0).getCutName());
    }
}
