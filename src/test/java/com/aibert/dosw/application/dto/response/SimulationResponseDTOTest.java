package com.aibert.dosw.application.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationResponseDTO Tests")
class SimulationResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(3.5)
            .isAchievable(true)
            .message("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (70% restante).")
            .build();

        assertThat(dto.getRequiredGrade()).isEqualTo(3.5);
        assertThat(dto.getIsAchievable()).isTrue();
        assertThat(dto.getMessage()).isEqualTo("Para alcanzar 4.0 necesitas obtener 3.50 o más en los cortes pendientes (70% restante).");
    }

    @Test
    @DisplayName("Should create with no-args constructor (all fields null)")
    void shouldCreateWithNoArgsConstructor() {
        SimulationResponseDTO dto = new SimulationResponseDTO();

        assertThat(dto.getRequiredGrade()).isNull();
        assertThat(dto.getIsAchievable()).isNull();
        assertThat(dto.getMessage()).isNull();
        assertThat(dto.getPendingCuts()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        SimulationResponseDTO dto = new SimulationResponseDTO(3.5, Boolean.TRUE, "Test message", null);

        assertThat(dto.getRequiredGrade()).isEqualTo(3.5);
        assertThat(dto.getIsAchievable()).isTrue();
        assertThat(dto.getMessage()).isEqualTo("Test message");
        assertThat(dto.getPendingCuts()).isNull();
    }

    @Test
    @DisplayName("Should set and get requiredGrade")
    void shouldSetAndGetRequiredGrade() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setRequiredGrade(4.2);
        assertThat(dto.getRequiredGrade()).isEqualTo(4.2);
    }

    @Test
    @DisplayName("Should set and get isAchievable")
    void shouldSetAndGetIsAchievable() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setIsAchievable(false);
        assertThat(dto.getIsAchievable()).isFalse();
    }

    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setMessage("Custom message");
        assertThat(dto.getMessage()).isEqualTo("Custom message");
    }

    @Test
    @DisplayName("Should handle unachievable scenario")
    void shouldHandleUnachievableScenario() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(6.0)
            .isAchievable(false)
            .message("No es posible alcanzar 5.0. La nota requerida (6.00) supera el máximo permitido (5.0).")
            .build();

        assertThat(dto.getRequiredGrade()).isEqualTo(6.0);
        assertThat(dto.getIsAchievable()).isFalse();
        assertThat(dto.getMessage()).isEqualTo("No es posible alcanzar 5.0. La nota requerida (6.00) supera el máximo permitido (5.0).");
    }

    @Test
    @DisplayName("Should handle zero required grade")
    void shouldHandleZeroRequiredGrade() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(0.0)
            .isAchievable(true)
            .message("¡Ya tienes asegurado superar tu meta!")
            .build();

        assertThat(dto.getRequiredGrade()).isEqualTo(0.0);
        assertThat(dto.getIsAchievable()).isTrue();
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .requiredGrade(4.0).isAchievable(true).message("Test message").build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        SimulationResponseDTO dto1 = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        SimulationResponseDTO dto2 = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null and contains key values")
    void shouldVerifyToString() {
        SimulationResponseDTO dto = SimulationResponseDTO.builder()
            .requiredGrade(3.5).isAchievable(true).message("Test message").build();
        String result = dto.toString();
        assertThat(result).isNotNull();
        assertThat(result).contains("3.5");
        assertThat(result).contains("true");
        assertThat(result).contains("Test message");
    }

    @Test
    void equalsWhenRequiredGradeDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().requiredGrade(3.5).isAchievable(true).message("M").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().requiredGrade(4.0).isAchievable(true).message("M").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenAchievableDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().requiredGrade(3.5).isAchievable(true).message("M").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().requiredGrade(3.5).isAchievable(false).message("M").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenMessageDiffers() {
        SimulationResponseDTO a = SimulationResponseDTO.builder().requiredGrade(3.5).isAchievable(true).message("A").build();
        SimulationResponseDTO b = SimulationResponseDTO.builder().requiredGrade(3.5).isAchievable(true).message("B").build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new SimulationResponseDTO()).isEqualTo(new SimulationResponseDTO());
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new SimulationResponseDTO().hashCode()).isEqualTo(new SimulationResponseDTO().hashCode());
    }
}
