package com.aibert.dosw.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulationResult Tests")
class SimulationResultTest {

    private SimulationResult simulationResult;

    @BeforeEach
    void setUp() {
        simulationResult = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
    }

    @Test
    @DisplayName("Should create SimulationResult using builder")
    void shouldCreateSimulationResultUsingBuilder() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(4.5)
            .requiredGrade(4.0)
            .achievable(true)
            .pendingPercentage(60.0)
            .build();

        // Then
        assertNotNull(result);
        assertEquals(4.5, result.getTargetGrade());
        assertEquals(4.0, result.getRequiredGrade());
        assertTrue(result.isAchievable());
        assertEquals(60.0, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should create SimulationResult using all-args constructor")
    void shouldCreateSimulationResultUsingAllArgsConstructor() {
        // When
        SimulationResult result = new SimulationResult(3.5, 3.0, false, 40.0);

        // Then
        assertNotNull(result);
        assertEquals(3.5, result.getTargetGrade());
        assertEquals(3.0, result.getRequiredGrade());
        assertFalse(result.isAchievable());
        assertEquals(40.0, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should create SimulationResult using no-args constructor")
    void shouldCreateSimulationResultUsingNoArgsConstructor() {
        // When
        SimulationResult result = new SimulationResult();

        // Then
        assertNotNull(result);
        assertNull(result.getTargetGrade());
        assertNull(result.getRequiredGrade());
        assertFalse(result.isAchievable());
        assertNull(result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should get target grade correctly")
    void shouldGetTargetGradeCorrectly() {
        // When
        Double targetGrade = simulationResult.getTargetGrade();

        // Then
        assertEquals(4.0, targetGrade);
    }

    @Test
    @DisplayName("Should get required grade correctly")
    void shouldGetRequiredGradeCorrectly() {
        // When
        Double requiredGrade = simulationResult.getRequiredGrade();

        // Then
        assertEquals(3.5, requiredGrade);
    }

    @Test
    @DisplayName("Should get achievable status correctly")
    void shouldGetAchievableStatusCorrectly() {
        // When
        boolean achievable = simulationResult.isAchievable();

        // Then
        assertTrue(achievable);
    }

    @Test
    @DisplayName("Should get pending percentage correctly")
    void shouldGetPendingPercentageCorrectly() {
        // When
        Double pendingPercentage = simulationResult.getPendingPercentage();

        // Then
        assertEquals(70.0, pendingPercentage);
    }

    @Test
    @DisplayName("Should handle null values in builder")
    void shouldHandleNullValuesInBuilder() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(null)
            .requiredGrade(null)
            .achievable(false)
            .pendingPercentage(null)
            .build();

        // Then
        assertNotNull(result);
        assertNull(result.getTargetGrade());
        assertNull(result.getRequiredGrade());
        assertFalse(result.isAchievable());
        assertNull(result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should handle boundary values")
    void shouldHandleBoundaryValues() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(0.0)
            .requiredGrade(5.0)
            .achievable(false)
            .pendingPercentage(100.0)
            .build();

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getTargetGrade());
        assertEquals(5.0, result.getRequiredGrade());
        assertFalse(result.isAchievable());
        assertEquals(100.0, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should handle negative values")
    void shouldHandleNegativeValues() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(-1.0)
            .requiredGrade(-2.0)
            .achievable(false)
            .pendingPercentage(-50.0)
            .build();

        // Then
        assertNotNull(result);
        assertEquals(-1.0, result.getTargetGrade());
        assertEquals(-2.0, result.getRequiredGrade());
        assertFalse(result.isAchievable());
        assertEquals(-50.0, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should handle very large values")
    void shouldHandleVeryLargeValues() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(Double.MAX_VALUE)
            .requiredGrade(Double.MAX_VALUE)
            .achievable(true)
            .pendingPercentage(Double.MAX_VALUE)
            .build();

        // Then
        assertNotNull(result);
        assertEquals(Double.MAX_VALUE, result.getTargetGrade());
        assertEquals(Double.MAX_VALUE, result.getRequiredGrade());
        assertTrue(result.isAchievable());
        assertEquals(Double.MAX_VALUE, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should handle very small values")
    void shouldHandleVerySmallValues() {
        // When
        SimulationResult result = SimulationResult.builder()
            .targetGrade(Double.MIN_VALUE)
            .requiredGrade(Double.MIN_VALUE)
            .achievable(true)
            .pendingPercentage(Double.MIN_VALUE)
            .build();

        // Then
        assertNotNull(result);
        assertEquals(Double.MIN_VALUE, result.getTargetGrade());
        assertEquals(Double.MIN_VALUE, result.getRequiredGrade());
        assertTrue(result.isAchievable());
        assertEquals(Double.MIN_VALUE, result.getPendingPercentage());
    }

    @Test
    @DisplayName("Should verify default equals method (reference equality)")
    void shouldVerifyDefaultEqualsMethod() {
        // Given
        SimulationResult result1 = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
        
        SimulationResult result2 = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();

        // Then - Default equals uses reference equality
        assertNotEquals(result1, result2);
        assertEquals(result1, result1);
        assertNotEquals(result1, null);
        assertNotEquals(result1, "string");
    }

    @Test
    @DisplayName("Should verify default hashCode method")
    void shouldVerifyDefaultHashCodeMethod() {
        // Given
        SimulationResult result1 = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
        
        SimulationResult result2 = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();

        // Then - Different objects have different hash codes by default
        assertNotEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    @DisplayName("Should verify default toString method")
    void shouldVerifyDefaultToStringMethod() {
        // When
        String toString = simulationResult.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("com.aibert.dosw.domain.model.SimulationResult"));
    }
}
