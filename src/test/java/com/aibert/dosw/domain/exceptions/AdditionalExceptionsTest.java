package com.aibert.dosw.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Additional Exception Tests")
class AdditionalExceptionsTest {

    @Test
    @DisplayName("Should create GradeNotFoundException with valid ID")
    void shouldCreateGradeNotFoundExceptionWithValidId() {
        // Given
        Long gradeId = 1L;

        // When
        GradeNotFoundException exception = new GradeNotFoundException(gradeId);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(gradeId.toString()));
    }

    @Test
    @DisplayName("Should create GradeOutOfRangeException with valid grade")
    void shouldCreateGradeOutOfRangeExceptionWithValidGrade() {
        // Given
        Double gradeValue = 5.5;

        // When
        GradeOutOfRangeException exception = new GradeOutOfRangeException(gradeValue);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(gradeValue.toString()));
    }

    @Test
    @DisplayName("Should create InvalidEvaluationStructureException with valid message")
    void shouldCreateInvalidEvaluationStructureExceptionWithValidMessage() {
        // Given
        String message = "Invalid evaluation structure";

        // When
        InvalidEvaluationStructureException exception = new InvalidEvaluationStructureException(message);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create NoPendingCutsException with valid ID")
    void shouldCreateNoPendingCutsExceptionWithValidId() {
        // Given
        Long subjectId = 42L;

        // When
        NoPendingCutsException exception = new NoPendingCutsException(subjectId);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(subjectId.toString()));
    }

    @Test
    @DisplayName("Should create SubjectNotFoundException with valid ID")
    void shouldCreateSubjectNotFoundExceptionWithValidId() {
        // Given
        Long subjectId = 999L;

        // When
        SubjectNotFoundException exception = new SubjectNotFoundException(subjectId);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains(subjectId.toString()));
    }

    @Test
    @DisplayName("Should handle null values in exceptions appropriately")
    void shouldHandleNullValuesInExceptionsAppropriately() {
        // Test GradeNotFoundException with null
        assertDoesNotThrow(() -> {
            GradeNotFoundException exception = new GradeNotFoundException(null);
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("null"));
        });

        // Test GradeOutOfRangeException with null
        assertDoesNotThrow(() -> {
            GradeOutOfRangeException exception = new GradeOutOfRangeException(null);
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("null"));
        });

        // Test NoPendingCutsException with null
        assertDoesNotThrow(() -> {
            NoPendingCutsException exception = new NoPendingCutsException(null);
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("null"));
        });

        // Test SubjectNotFoundException with null
        assertDoesNotThrow(() -> {
            SubjectNotFoundException exception = new SubjectNotFoundException(null);
            assertNotNull(exception);
            assertTrue(exception.getMessage().contains("null"));
        });
    }

    @Test
    @DisplayName("Should handle boundary values in exceptions")
    void shouldHandleBoundaryValuesInExceptions() {
        // Test GradeNotFoundException with boundary values
        assertDoesNotThrow(() -> {
            new GradeNotFoundException(Long.MIN_VALUE);
            new GradeNotFoundException(Long.MAX_VALUE);
            new GradeNotFoundException(0L);
        });

        // Test GradeOutOfRangeException with boundary values
        assertDoesNotThrow(() -> {
            new GradeOutOfRangeException(Double.MIN_VALUE);
            new GradeOutOfRangeException(Double.MAX_VALUE);
            new GradeOutOfRangeException(0.0);
        });

        // Test NoPendingCutsException with boundary values
        assertDoesNotThrow(() -> {
            new NoPendingCutsException(Long.MIN_VALUE);
            new NoPendingCutsException(Long.MAX_VALUE);
            new NoPendingCutsException(0L);
        });

        // Test SubjectNotFoundException with boundary values
        assertDoesNotThrow(() -> {
            new SubjectNotFoundException(Long.MIN_VALUE);
            new SubjectNotFoundException(Long.MAX_VALUE);
            new SubjectNotFoundException(0L);
        });
    }

    @Test
    @DisplayName("Should verify all exceptions extend RuntimeException")
    void shouldVerifyAllExceptionsExtendRuntimeException() {
        // Test GradeNotFoundException
        GradeNotFoundException gradeNotFound = new GradeNotFoundException(1L);
        assertTrue(gradeNotFound instanceof RuntimeException);

        // Test GradeOutOfRangeException
        GradeOutOfRangeException gradeOutOfRange = new GradeOutOfRangeException(5.5);
        assertTrue(gradeOutOfRange instanceof RuntimeException);

        // Test InvalidEvaluationStructureException
        InvalidEvaluationStructureException invalidStructure = new InvalidEvaluationStructureException("test");
        assertTrue(invalidStructure instanceof RuntimeException);

        // Test NoPendingCutsException
        NoPendingCutsException noPendingCuts = new NoPendingCutsException(1L);
        assertTrue(noPendingCuts instanceof RuntimeException);

        // Test SubjectNotFoundException
        SubjectNotFoundException subjectNotFound = new SubjectNotFoundException(1L);
        assertTrue(subjectNotFound instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should verify exception messages contain expected content")
    void shouldVerifyExceptionMessagesContainExpectedContent() {
        // Test GradeNotFoundException message
        GradeNotFoundException gradeNotFound = new GradeNotFoundException(123L);
        assertTrue(gradeNotFound.getMessage().contains("123"));

        // Test GradeOutOfRangeException message
        GradeOutOfRangeException gradeOutOfRange = new GradeOutOfRangeException(4.5);
        assertTrue(gradeOutOfRange.getMessage().contains("4.5"));

        // Test NoPendingCutsException message
        NoPendingCutsException noPendingCuts = new NoPendingCutsException(456L);
        assertTrue(noPendingCuts.getMessage().contains("456"));

        // Test SubjectNotFoundException message
        SubjectNotFoundException subjectNotFound = new SubjectNotFoundException(789L);
        assertTrue(subjectNotFound.getMessage().contains("789"));
    }

    @Test
    @DisplayName("Should verify exceptions have no cause by default")
    void shouldVerifyExceptionsHaveNoCauseByDefault() {
        // Test all exceptions have no cause
        assertNull(new GradeNotFoundException(1L).getCause());
        assertNull(new GradeOutOfRangeException(5.5).getCause());
        assertNull(new InvalidEvaluationStructureException("test").getCause());
        assertNull(new NoPendingCutsException(1L).getCause());
        assertNull(new SubjectNotFoundException(1L).getCause());
    }
}
