package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.application.dto.response.EvaluationCutResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EvaluationStructureResponseDTO Tests")
class EvaluationStructureResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of(
            EvaluationCutResponseDTO.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCutResponseDTO.builder()
                .id(2L)
                .cutName("Partial 2")
                .cutPercentage(30.0)
                .grade(3.5)
                .build()
        );

        // When
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts()).hasSize(2);
        assertThat(dto.getEvaluationCuts().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getEvaluationCuts().get(1).getId()).isEqualTo(2L);
        assertThat(dto.getEvaluationCuts().get(1).getCutName()).isEqualTo("Partial 2");
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        EvaluationStructureResponseDTO dto = new EvaluationStructureResponseDTO();

        // Then
        assertThat(dto.getSubjectId()).isNull();
        assertThat(dto.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of();

        // When
        EvaluationStructureResponseDTO dto = new EvaluationStructureResponseDTO(1L, evaluationCuts);

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts()).isSameAs(evaluationCuts);
    }

    @Test
    @DisplayName("Should set and get subjectId")
    void shouldSetAndGetSubjectId() {
        // Given
        EvaluationStructureResponseDTO dto = new EvaluationStructureResponseDTO();

        // When
        dto.setSubjectId(2L);

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get evaluationCuts")
    void shouldSetAndGetEvaluationCuts() {
        // Given
        EvaluationStructureResponseDTO dto = new EvaluationStructureResponseDTO();
        List<EvaluationCutResponseDTO> evaluationCuts = List.of(
            EvaluationCutResponseDTO.builder()
                .id(1L)
                .cutName("Final")
                .cutPercentage(40.0)
                .grade(3.8)
                .build()
        );

        // When
        dto.setEvaluationCuts(evaluationCuts);

        // Then
        assertThat(dto.getEvaluationCuts()).hasSize(1);
        assertThat(dto.getEvaluationCuts().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts().get(0).getCutName()).isEqualTo("Final");
    }

    @Test
    @DisplayName("Should handle null evaluationCuts")
    void shouldHandleNullEvaluationCuts() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(null)
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should handle empty evaluationCuts")
    void shouldHandleEmptyEvaluationCuts() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getEvaluationCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple evaluationCuts")
    void shouldHandleMultipleEvaluationCuts() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of(
            EvaluationCutResponseDTO.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(25.0)
                .grade(4.0)
                .build(),
            EvaluationCutResponseDTO.builder()
                .id(2L)
                .cutName("Partial 2")
                .cutPercentage(25.0)
                .grade(3.5)
                .build(),
            EvaluationCutResponseDTO.builder()
                .id(3L)
                .cutName("Final")
                .cutPercentage(50.0)
                .grade(4.2)
                .build()
        );

        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
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
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of();
        EvaluationStructureResponseDTO dto1 = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureResponseDTO dto2 = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of();
        EvaluationStructureResponseDTO dto1 = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureResponseDTO dto2 = EvaluationStructureResponseDTO.builder()
            .subjectId(2L)
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        List<EvaluationCutResponseDTO> evaluationCuts = List.of();
        EvaluationStructureResponseDTO dto1 = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();
        EvaluationStructureResponseDTO dto2 = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
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
        EvaluationStructureResponseDTO dto = EvaluationStructureResponseDTO.builder()
            .subjectId(1L)
            .evaluationCuts(List.of())
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("1");
    }
}
