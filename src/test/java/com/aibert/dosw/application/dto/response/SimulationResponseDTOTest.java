package com.aibert.dosw.application.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationResponseDTO Tests")
class SimulationResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // When
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (70% restante).")
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(4.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(3.5);
        assertThat(dto.isAchievable()).isTrue();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(70.0);
        assertThat(dto.getMessage()).isEqualTo("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (70% restante).");
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // Then
        assertThat(dto.getTargetGrade()).isNull();
        assertThat(dto.getRequiredGrade()).isNull();
        assertThat(dto.isAchievable()).isFalse();
        assertThat(dto.getPendingCutsPercentage()).isNull();
        assertThat(dto.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // When
        SimulationResponseDTO dto = new SimulationResponseDTO(4.0, 3.5, true, 70.0, "Test message", null);

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(4.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(3.5);
        assertThat(dto.isAchievable()).isTrue();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(70.0);
        assertThat(dto.getMessage()).isEqualTo("Test message");
        assertThat(dto.getPendingCuts()).isNull();
    }

    @Test
    @DisplayName("Should set and get targetGrade")
    void shouldSetAndGetTargetGrade() {
        // Given
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // When
        dto.setTargetGrade(3.5);

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get requiredGrade")
    void shouldSetAndGetRequiredGrade() {
        // Given
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // When
        dto.setRequiredGrade(4.2);

        // Then
        assertThat(dto.getRequiredGrade()).isEqualTo(4.2);
    }

    @Test
    @DisplayName("Should set and get achievable")
    void shouldSetAndGetAchievable() {
        // Given
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // When
        dto.setAchievable(false);

        // Then
        assertThat(dto.isAchievable()).isFalse();
    }

    @Test
    @DisplayName("Should set and get pendingCutsPercentage")
    void shouldSetAndGetPendingCutsPercentage() {
        // Given
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // When
        dto.setPendingCutsPercentage(85.5);

        // Then
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(85.5);
    }

    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        // Given
        SimulationResponseDTO dto = new SimulationResponseDTO();

        // When
        dto.setMessage("Custom message");

        // Then
        assertThat(dto.getMessage()).isEqualTo("Custom message");
    }

    @Test
    @DisplayName("Should handle unachievable scenario")
    void shouldHandleUnachievableScenario() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(5.0)
            .requiredGrade(6.0)
            .achievable(false)
            .pendingCutsPercentage(50.0)
            .message("No es posible alcanzar 5.0. La nota requerida (6.00) supera el máximo permitido (5.0).")
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(5.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(6.0);
        assertThat(dto.isAchievable()).isFalse();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(50.0);
        assertThat(dto.getMessage()).isEqualTo("No es posible alcanzar 5.0. La nota requerida (6.00) supera el máximo permitido (5.0).");
    }

    @Test
    @DisplayName("Should handle zero required grade")
    void shouldHandleZeroRequiredGrade() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(3.0)
            .requiredGrade(0.0)
            .achievable(true)
            .pendingCutsPercentage(25.0)
            .message("¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás 3.0.")
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(3.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(0.0);
        assertThat(dto.isAchievable()).isTrue();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(25.0);
        assertThat(dto.getMessage()).isEqualTo("¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás 3.0.");
    }

    @Test
    @DisplayName("Should handle zero pending percentage")
    void shouldHandleZeroPendingPercentage() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(0.0)
            .message("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (0% restante).")
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(4.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(3.5);
        assertThat(dto.isAchievable()).isTrue();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(0.0);
        assertThat(dto.getMessage()).isEqualTo("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (0% restante).");
    }

    @Test
    @DisplayName("Should handle maximum values")
    void shouldHandleMaximumValues() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(5.0)
            .requiredGrade(5.0)
            .achievable(true)
            .pendingCutsPercentage(100.0)
            .message("Para alcanzar 5.0 necesitas obtener 5.00 o más en los cortes pendientes (100% restante).")
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(5.0);
        assertThat(dto.getRequiredGrade()).isEqualTo(5.0);
        assertThat(dto.isAchievable()).isTrue();
        assertThat(dto.getPendingCutsPercentage()).isEqualTo(100.0);
        assertThat(dto.getMessage()).isEqualTo("Para alcanzar 5.0 necesitas obtener 5.00 o más en los cortes pendientes (100% restante).");
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .targetGrade(3.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should verify toString contains key values")
    void shouldVerifyToStringContainsKeyValues() {
        // Given
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingCutsPercentage(70.0)
            .message("Test message")
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("4.0");
        assertThat(result).contains("3.5");
        assertThat(result).contains("true");
        assertThat(result).contains("70.0");
        assertThat(result).contains("Test message");
    }

    @Test
    void equalsWhenRequiredGradeDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(70.0).message("M").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(4.0).achievable(true).pendingCutsPercentage(70.0).message("M").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenAchievableDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(70.0).message("M").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(false).pendingCutsPercentage(70.0).message("M").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenPendingCutsPercentageDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(70.0).message("M").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(80.0).message("M").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenMessageDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(70.0).message("A").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().targetGrade(4.0).requiredGrade(3.5).achievable(true).pendingCutsPercentage(70.0).message("B").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new SimulationResponseDTO()).isEqualTo(new SimulationResponseDTO());
    }

    @Test
    void equalsWithNullTargetGradeVsNonNull() {
        SimulationResponseDTO withNull = new SimulationResponseDTO();
        SimulationResponseDTO withValue = SimulationResponseDTO.builder().targetGrade(4.0).build();
        assertThat(withNull).isNotEqualTo(withValue);
        assertThat(withValue).isNotEqualTo(withNull);
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new SimulationResponseDTO().hashCode()).isEqualTo(new SimulationResponseDTO().hashCode());
    }
}
