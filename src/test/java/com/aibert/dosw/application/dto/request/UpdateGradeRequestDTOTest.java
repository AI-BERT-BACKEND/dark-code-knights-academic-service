package com.aibert.dosw.application.dto.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UpdateGradeRequestDTO Tests")
class UpdateGradeRequestDTOTest {

    private final Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // When
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(dto.getGradeValue()).isEqualTo(4.0);
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        UpdateGradeRequestDTO dto = new UpdateGradeRequestDTO();

        // Then
        assertThat(dto.getActivityName()).isNull();
        assertThat(dto.getGradeValue()).isNull();
        assertThat(dto.getPercentage()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // When
        UpdateGradeRequestDTO dto = new UpdateGradeRequestDTO("Final Exam", 3.8, 40.0);

        // Then
        assertThat(dto.getActivityName()).isEqualTo("Final Exam");
        assertThat(dto.getGradeValue()).isEqualTo(3.8);
        assertThat(dto.getPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should set and get activityName")
    void shouldSetAndGetActivityName() {
        // Given
        UpdateGradeRequestDTO dto = new UpdateGradeRequestDTO();

        // When
        dto.setActivityName("Quiz");

        // Then
        assertThat(dto.getActivityName()).isEqualTo("Quiz");
    }

    @Test
    @DisplayName("Should set and get gradeValue")
    void shouldSetAndGetGradeValue() {
        // Given
        UpdateGradeRequestDTO dto = new UpdateGradeRequestDTO();

        // When
        dto.setGradeValue(3.5);

        // Then
        assertThat(dto.getGradeValue()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get percentage")
    void shouldSetAndGetPercentage() {
        // Given
        UpdateGradeRequestDTO dto = new UpdateGradeRequestDTO();

        // When
        dto.setPercentage(25.0);

        // Then
        assertThat(dto.getPercentage()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Should validate valid activityName")
    void shouldValidateValidActivityName() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate blank activityName")
    void shouldValidateBlankActivityName() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("El nombre de la actividad es obligatorio");
    }

    @Test
    @DisplayName("Should validate null activityName")
    void shouldValidateNullActivityName() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName(null)
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("El nombre de la actividad es obligatorio");
    }

    @Test
    @DisplayName("Should validate null gradeValue")
    void shouldValidateNullGradeValue() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(null)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota es obligatoria");
    }

    @Test
    @DisplayName("Should validate gradeValue below minimum")
    void shouldValidateGradeValueBelowMinimum() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(-0.1)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota mínima es 0.0");
    }

    @Test
    @DisplayName("Should validate gradeValue above maximum")
    void shouldValidateGradeValueAboveMaximum() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(5.1)
            .percentage(30.0)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La nota máxima es 5.0");
    }

    @Test
    @DisplayName("Should validate null percentage")
    void shouldValidateNullPercentage() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(4.0)
            .percentage(null)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("El porcentaje de la actividad es obligatorio");
    }

    @Test
    @DisplayName("Should validate percentage below minimum")
    void shouldValidatePercentageBelowMinimum() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(4.0)
            .percentage(0.05)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("El porcentaje debe ser mayor a 0");
    }

    @Test
    @DisplayName("Should validate percentage above maximum")
    void shouldValidatePercentageAboveMaximum() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Valid Activity")
            .gradeValue(4.0)
            .percentage(100.1)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("El porcentaje no puede superar 100");
    }

    @Test
    @DisplayName("Should handle boundary values")
    void shouldHandleBoundaryValues() {
        // Given
        UpdateGradeRequestDTO dto1 = UpdateGradeRequestDTO.builder()
            .activityName("Min Boundary")
            .gradeValue(0.0)
            .percentage(0.1)
            .build();
        UpdateGradeRequestDTO dto2 = UpdateGradeRequestDTO.builder()
            .activityName("Max Boundary")
            .gradeValue(5.0)
            .percentage(99.9)
            .build();

        // When
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations1 = validator.validate(dto1);
        Set<ConstraintViolation<UpdateGradeRequestDTO>> violations2 = validator.validate(dto2);

        // Then
        assertThat(violations1).isEmpty();
        assertThat(violations2).isEmpty();
        assertThat(dto1.getActivityName()).isEqualTo("Min Boundary");
        assertThat(dto1.getGradeValue()).isEqualTo(0.0);
        assertThat(dto1.getPercentage()).isEqualTo(0.1);
        assertThat(dto2.getActivityName()).isEqualTo("Max Boundary");
        assertThat(dto2.getGradeValue()).isEqualTo(5.0);
        assertThat(dto2.getPercentage()).isEqualTo(99.9);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        UpdateGradeRequestDTO dto1 = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        UpdateGradeRequestDTO dto2 = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        UpdateGradeRequestDTO dto1 = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        UpdateGradeRequestDTO dto2 = UpdateGradeRequestDTO.builder()
            .activityName("Different Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        UpdateGradeRequestDTO dto1 = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        UpdateGradeRequestDTO dto2 = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
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
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Test Activity")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("Test Activity");
        assertThat(result).contains("4.0");
        assertThat(result).contains("30.0");
    }
}
