package com.aibert.dosw.application.dto.request;

import com.aibert.dosw.application.dto.request.EvaluationCutDTO;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EvaluationStructureRequestDTO Tests")
class EvaluationStructureRequestDTOTest {

    private final Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of(
            EvaluationCutDTO.builder()
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .build()
        );

        // When
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto.getEvaluationCuts()).hasSize(1);
        assertThat(dto.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        EvaluationStructureRequestDTO dto = new EvaluationStructureRequestDTO();

        // Then
        assertThat(dto.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();

        // When
        EvaluationStructureRequestDTO dto = new EvaluationStructureRequestDTO(evaluationCuts);

        // Then
        assertThat(dto.getEvaluationCuts()).isSameAs(evaluationCuts);
    }

    @Test
    @DisplayName("Should set and get evaluationCuts")
    void shouldSetAndGetEvaluationCuts() {
        // Given
        EvaluationStructureRequestDTO dto = new EvaluationStructureRequestDTO();
        List<EvaluationCutDTO> evaluationCuts = List.of(
            EvaluationCutDTO.builder()
                .cutName("Final")
                .cutPercentage(40.0)
                .build()
        );

        // When
        dto.setEvaluationCuts(evaluationCuts);

        // Then
        assertThat(dto.getEvaluationCuts()).hasSize(1);
        assertThat(dto.getEvaluationCuts().get(0).getCutName()).isEqualTo("Final");
        assertThat(dto.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should handle null evaluationCuts")
    void shouldHandleNullEvaluationCuts() {
        // Given
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(null)
            .build();

        // Then
        assertThat(dto.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should handle empty evaluationCuts")
    void shouldHandleEmptyEvaluationCuts() {
        // Given
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(dto.getEvaluationCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple evaluationCuts")
    void shouldHandleMultipleEvaluationCuts() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of(
            EvaluationCutDTO.builder()
                .cutName("Partial 1")
                .cutPercentage(25.0)
                .build(),
            EvaluationCutDTO.builder()
                .cutName("Partial 2")
                .cutPercentage(25.0)
                .build(),
            EvaluationCutDTO.builder()
                .cutName("Final")
                .cutPercentage(50.0)
                .build()
        );

        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto.getEvaluationCuts()).hasSize(3);
        assertThat(dto.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getEvaluationCuts().get(1).getCutName()).isEqualTo("Partial 2");
        assertThat(dto.getEvaluationCuts().get(2).getCutName()).isEqualTo("Final");
        assertThat(dto.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(25.0);
        assertThat(dto.getEvaluationCuts().get(1).getCutPercentage()).isEqualTo(25.0);
        assertThat(dto.getEvaluationCuts().get(2).getCutPercentage()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should validate non-empty evaluationCuts")
    void shouldValidateNonEmptyEvaluationCuts() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of(
            EvaluationCutDTO.builder()
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .build()
        );

        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // When
        Set<ConstraintViolation<EvaluationStructureRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate empty evaluationCuts")
    void shouldValidateEmptyEvaluationCuts() {
        // Given
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(List.of())
            .build();

        // When
        Set<ConstraintViolation<EvaluationStructureRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("La materia debe tener al menos un corte evaluativo");
    }

    @Test
    @DisplayName("Should validate null evaluationCuts")
    void shouldValidateNullEvaluationCuts() {
        // Given
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(null)
            .build();

        // When
        Set<ConstraintViolation<EvaluationStructureRequestDTO>> violations = validator.validate(dto);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations.iterator().next().getMessage()).contains("La materia debe tener al menos un corte evaluativo");
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto1 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureRequestDTO dto2 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto1 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureRequestDTO dto2 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(List.of(
                EvaluationCutDTO.builder()
                    .cutName("Different")
                    .cutPercentage(30.0)
                    .build()
        ))
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto1 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureRequestDTO dto2 = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        List<EvaluationCutDTO> evaluationCuts = List.of();
        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
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
        List<EvaluationCutDTO> evaluationCuts = List.of(
            EvaluationCutDTO.builder()
                .cutName("Test Cut")
                .cutPercentage(30.0)
                .build()
        );

        EvaluationStructureRequestDTO dto = EvaluationStructureRequestDTO.builder()
            .evaluationCuts(evaluationCuts)
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("Test Cut");
        assertThat(result).contains("30.0");
    }
}
