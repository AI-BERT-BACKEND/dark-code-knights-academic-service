package com.aibert.dosw.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubjectNotFoundException Tests")
class SubjectNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        Long subjectId = 1L;
        SubjectNotFoundException exception = new SubjectNotFoundException(subjectId);
        
        assertNotNull(exception);
        assertEquals("Materia con id 1 no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with different subject id")
    void shouldCreateExceptionWithDifferentSubjectId() {
        Long subjectId = 999L;
        SubjectNotFoundException exception = new SubjectNotFoundException(subjectId);
        
        assertNotNull(exception);
        assertEquals("Materia con id 999 no encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Should extend RuntimeException")
    void shouldExtendRuntimeException() {
        SubjectNotFoundException exception = new SubjectNotFoundException(1L);
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should have correct exception type")
    void shouldHaveCorrectExceptionType() {
        assertInstanceOf(SubjectNotFoundException.class, new SubjectNotFoundException(1L));
        assertEquals("SubjectNotFoundException", SubjectNotFoundException.class.getSimpleName());
    }
}
