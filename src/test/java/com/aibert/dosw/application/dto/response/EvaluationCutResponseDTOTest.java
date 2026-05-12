package com.aibert.dosw.application.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EvaluationCutResponseDTO Tests")
class EvaluationCutResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // When
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getCutName()).isNull();
        assertThat(dto.getCutPercentage()).isNull();
        assertThat(dto.getGrade()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // When
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO(1L, "Partial 1", 30.0, 4.0);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO();

        // When
        dto.setId(2L);

        // Then
        assertThat(dto.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get cutName")
    void shouldSetAndGetCutName() {
        // Given
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO();

        // When
        dto.setCutName("Final Exam");

        // Then
        assertThat(dto.getCutName()).isEqualTo("Final Exam");
    }

    @Test
    @DisplayName("Should set and get cutPercentage")
    void shouldSetAndGetCutPercentage() {
        // Given
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO();

        // When
        dto.setCutPercentage(40.0);

        // Then
        assertThat(dto.getCutPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should set and get grade")
    void shouldSetAndGetGrade() {
        // Given
        EvaluationCutResponseDTO dto = new EvaluationCutResponseDTO();

        // When
        dto.setGrade(3.5);

        // Then
        assertThat(dto.getGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should handle null grade")
    void shouldHandleNullGrade() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getGrade()).isNull();
    }

    @Test
    @DisplayName("Should handle zero grade")
    void shouldHandleZeroGrade() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(0.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getGrade()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should handle maximum grade")
    void shouldHandleMaximumGrade() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(5.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getGrade()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        EvaluationCutResponseDTO dto1 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
        EvaluationCutResponseDTO dto2 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        EvaluationCutResponseDTO dto1 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
        EvaluationCutResponseDTO dto2 = EvaluationCutResponseDTO.builder()
            .id(2L)
            .cutName("Partial 2")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        EvaluationCutResponseDTO dto1 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
        EvaluationCutResponseDTO dto2 = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
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
        EvaluationCutResponseDTO dto = EvaluationCutResponseDTO.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("1");
        assertThat(result).contains("Partial 1");
        assertThat(result).contains("30.0");
        assertThat(result).contains("4.0");
    }

    @Test
    void equalsWhenCutNameDiffers() {
        EvaluationCutResponseDTO a = EvaluationCutResponseDTO.builder().id(1L).cutName("A").cutPercentage(30.0).grade(4.0).build();
        EvaluationCutResponseDTO b = EvaluationCutResponseDTO.builder().id(1L).cutName("B").cutPercentage(30.0).grade(4.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenCutPercentageDiffers() {
        EvaluationCutResponseDTO a = EvaluationCutResponseDTO.builder().id(1L).cutName("A").cutPercentage(30.0).grade(4.0).build();
        EvaluationCutResponseDTO b = EvaluationCutResponseDTO.builder().id(1L).cutName("A").cutPercentage(40.0).grade(4.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenGradeDiffers() {
        EvaluationCutResponseDTO a = EvaluationCutResponseDTO.builder().id(1L).cutName("A").cutPercentage(30.0).grade(4.0).build();
        EvaluationCutResponseDTO b = EvaluationCutResponseDTO.builder().id(1L).cutName("A").cutPercentage(30.0).grade(5.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new EvaluationCutResponseDTO()).isEqualTo(new EvaluationCutResponseDTO());
    }

    @Test
    void equalsWithNullIdVsNonNull() {
        EvaluationCutResponseDTO withNull = new EvaluationCutResponseDTO();
        EvaluationCutResponseDTO withValue = EvaluationCutResponseDTO.builder().id(1L).build();
        assertThat(withNull).isNotEqualTo(withValue);
        assertThat(withValue).isNotEqualTo(withNull);
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new EvaluationCutResponseDTO().hashCode()).isEqualTo(new EvaluationCutResponseDTO().hashCode());
    }
}
