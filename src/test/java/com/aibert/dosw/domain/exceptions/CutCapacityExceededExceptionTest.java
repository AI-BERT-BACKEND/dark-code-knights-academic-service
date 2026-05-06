package com.aibert.dosw.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CutCapacityExceededException Tests")
class CutCapacityExceededExceptionTest {

    @Test
    @DisplayName("Should create exception with valid parameters")
    void shouldCreateExceptionWithValidParameters() {
        // Given
        Double currentTotal = 80.0;
        Double newPercentage = 30.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        assertNotNull(exception);
        assertTrue(exception instanceof RuntimeException);
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with correct message")
    void shouldCreateExceptionWithCorrectMessage() {
        // Given
        Double currentTotal = 85.5;
        Double newPercentage = 20.0;
        Double expectedTotal = currentTotal + newPercentage;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("No es posible agregar la actividad"));
        assertTrue(message.contains("la suma de porcentajes del corte quedaría en"));
        assertTrue(message.contains(expectedTotal.toString()));
        assertTrue(message.contains("máximo 100%"));
    }

    @Test
    @DisplayName("Should handle boundary values")
    void shouldHandleBoundaryValues() {
        // Given
        Double currentTotal = 99.9;
        Double newPercentage = 0.2;
        Double expectedTotal = 100.1;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        String message = exception.getMessage();
        assertTrue(message.contains(expectedTotal.toString()));
    }

    @Test
    @DisplayName("Should handle zero values")
    void shouldHandleZeroValues() {
        // Given
        Double currentTotal = 0.0;
        Double newPercentage = 0.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        String message = exception.getMessage();
        assertTrue(message.contains("0.0%"));
    }

    @Test
    @DisplayName("Should handle negative values")
    void shouldHandleNegativeValues() {
        // Given
        Double currentTotal = -10.0;
        Double newPercentage = -5.0;
        Double expectedTotal = -15.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        String message = exception.getMessage();
        assertTrue(message.contains(expectedTotal.toString()));
    }

    @Test
    @DisplayName("Should handle very large values")
    void shouldHandleVeryLargeValues() {
        // Given
        Double currentTotal = Double.MAX_VALUE;
        Double newPercentage = 100.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        Double currentTotal = null;
        Double newPercentage = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new CutCapacityExceededException(currentTotal, newPercentage);
        });
    }

    @Test
    @DisplayName("Should handle null current total")
    void shouldHandleNullCurrentTotal() {
        // Given
        Double currentTotal = null;
        Double newPercentage = 50.0;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new CutCapacityExceededException(currentTotal, newPercentage);
        });
    }

    @Test
    @DisplayName("Should handle null new percentage")
    void shouldHandleNullNewPercentage() {
        // Given
        Double currentTotal = 50.0;
        Double newPercentage = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            new CutCapacityExceededException(currentTotal, newPercentage);
        });
    }

    @Test
    @DisplayName("Should extend RuntimeException")
    void shouldExtendRuntimeException() {
        // Given
        Double currentTotal = 80.0;
        Double newPercentage = 30.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        assertTrue(exception instanceof RuntimeException);
        RuntimeException runtimeException = exception;
        assertNotNull(runtimeException.getMessage());
    }

    @Test
    @DisplayName("Should preserve exception cause (none)")
    void shouldPreserveExceptionCause() {
        // Given
        Double currentTotal = 80.0;
        Double newPercentage = 30.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Should handle decimal precision correctly")
    void shouldHandleDecimalPrecisionCorrectly() {
        // Given
        Double currentTotal = 66.66666666666667;
        Double newPercentage = 33.33333333333333;
        Double expectedTotal = 100.0;

        // When
        CutCapacityExceededException exception = new CutCapacityExceededException(currentTotal, newPercentage);

        // Then
        String message = exception.getMessage();
        // The exact decimal representation may vary due to floating point arithmetic
        assertTrue(message.contains("100.0") || message.contains("99.99999999999999") || message.contains("100.00000000000001"));
    }
}
