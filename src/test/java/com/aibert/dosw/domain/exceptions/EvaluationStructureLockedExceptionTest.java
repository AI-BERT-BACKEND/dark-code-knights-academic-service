package com.aibert.dosw.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationStructureLockedException Tests")
class EvaluationStructureLockedExceptionTest {

    @Test
    @DisplayName("Should create exception with valid subject ID")
    void shouldCreateExceptionWithValidSubjectId() {
        // Given
        Long subjectId = 1L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with correct message")
    void shouldCreateExceptionWithCorrectMessage() {
        // Given
        Long subjectId = 42L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("La estructura de evaluación de la materia"));
        assertTrue(message.contains(subjectId.toString()));
        assertTrue(message.contains("no puede editarse porque ya tiene notas registradas"));
    }

    @Test
    @DisplayName("Should handle zero subject ID")
    void shouldHandleZeroSubjectId() {
        // Given
        Long subjectId = 0L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        String message = exception.getMessage();
        assertTrue(message.contains("0"));
    }

    @Test
    @DisplayName("Should handle negative subject ID")
    void shouldHandleNegativeSubjectId() {
        // Given
        Long subjectId = -1L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        String message = exception.getMessage();
        assertTrue(message.contains("-1"));
    }

    @Test
    @DisplayName("Should handle very large subject ID")
    void shouldHandleVeryLargeSubjectId() {
        // Given
        Long subjectId = Long.MAX_VALUE;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(subjectId.toString()));
    }

    @Test
    @DisplayName("Should handle very small subject ID")
    void shouldHandleVerySmallSubjectId() {
        // Given
        Long subjectId = Long.MIN_VALUE;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(subjectId.toString()));
    }

    @Test
    @DisplayName("Should handle null subject ID")
    void shouldHandleNullSubjectId() {
        // Given
        Long subjectId = null;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    @DisplayName("Should extend RuntimeException")
    void shouldExtendRuntimeException() {
        // Given
        Long subjectId = 1L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertTrue(exception instanceof RuntimeException);
        RuntimeException runtimeException = exception;
        assertNotNull(runtimeException.getMessage());
    }

    @Test
    @DisplayName("Should preserve exception cause (none)")
    void shouldPreserveExceptionCause() {
        // Given
        Long subjectId = 1L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should handle typical subject ID values")
    void shouldHandleTypicalSubjectIdValues() {
        // Given
        Long[] subjectIds = {1L, 2L, 100L, 999L, 12345L};

        // When & Then
        for (Long subjectId : subjectIds) {
            EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains(subjectId.toString()));
        }
    }

    @Test
    @DisplayName("Should create exception with consistent message format")
    void shouldCreateExceptionWithConsistentMessageFormat() {
        // Given
        Long subjectId = 123L;

        // When
        EvaluationStructureLockedException exception = new EvaluationStructureLockedException(subjectId);

        // Then
        String message = exception.getMessage();
        String expectedPattern = "La estructura de evaluación de la materia " + subjectId + " no puede editarse porque ya tiene notas registradas";
        assertEquals(expectedPattern, message);
    }
}
