package com.aibert.dosw.application.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationRequestDTO Tests")
class SimulationRequestDTOTest {

    private final Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // When
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        SimulationRequestDTO dto = new SimulationRequestDTO();

        // Then
        assertThat(dto.getTargetGrade()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // When
        SimulationRequestDTO dto = new SimulationRequestDTO(3.5);

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get targetGrade")
    void shouldSetAndGetTargetGrade() {
        // Given
        SimulationRequestDTO dto = new SimulationRequestDTO();

        // When
        dto.setTargetGrade(4.5);

        // Then
        assertThat(dto.getTargetGrade()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("Should validate valid targetGrade")
    void shouldValidateValidTargetGrade() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(3.5)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate null targetGrade")
    void shouldValidateNullTargetGrade() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(null)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota objetivo no puede ser nula");
    }

    @Test
    @DisplayName("Should validate targetGrade below minimum")
    void shouldValidateTargetGradeBelowMinimum() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(-0.1)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota objetivo debe ser al menos 0.0");
    }

    @Test
    @DisplayName("Should validate targetGrade above maximum")
    void shouldValidateTargetGradeAboveMaximum() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(5.1)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota objetivo no puede superar 5.0");
    }

    @Test
    @DisplayName("Should handle boundary values")
    void shouldHandleBoundaryValues() {
        // Given
        SimulationRequestDTO dto1 = SimulationRequestDTO.builder()
            .targetGrade(0.0)
            .build();
        SimulationRequestDTO dto2 = SimulationRequestDTO.builder()
            .targetGrade(5.0)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations1 = validator.validate(dto1);
        Set<ConstraintViolation<SimulationRequestDTO>> violations2 = validator.validate(dto2);

        // Then
        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
        assertThat(dto1.getTargetGrade()).isEqualTo(0.0);
        assertThat(dto2.getTargetGrade()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should handle decimal targetGrade")
    void shouldHandleDecimalTargetGrade() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(3.75)
            .build();

        // When
        Set<ConstraintViolation<SimulationRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
        assertThat(dto.getTargetGrade()).isEqualTo(3.75);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        SimulationRequestDTO dto1 = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();
        SimulationRequestDTO dto2 = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        SimulationRequestDTO dto1 = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();
        SimulationRequestDTO dto2 = SimulationRequestDTO.builder()
            .targetGrade(3.0)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        SimulationRequestDTO dto1 = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();
        SimulationRequestDTO dto2 = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
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
        SimulationRequestDTO dto = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("4.0");
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new SimulationRequestDTO()).isEqualTo(new SimulationRequestDTO());
    }

    @Test
    void equalsWithNullTargetGradeVsNonNull() {
        SimulationRequestDTO withNull = new SimulationRequestDTO();
        SimulationRequestDTO withValue = SimulationRequestDTO.builder().targetGrade(4.0).build();
        assertThat(withNull).isNotEqualTo(withValue);
        assertThat(withValue).isNotEqualTo(withNull);
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new SimulationRequestDTO().hashCode()).isEqualTo(new SimulationRequestDTO().hashCode());
    }
}
