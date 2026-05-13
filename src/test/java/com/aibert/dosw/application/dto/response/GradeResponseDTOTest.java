package com.aibert.dosw.application.dto.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GradeResponseDTO Tests")
class GradeResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // When
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutId()).isEqualTo(2L);
        assertThat(dto.getActivityName()).isEqualTo("Partial Exam");
        assertThat(dto.getGradeValue()).isEqualTo(4.0);
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        GradeResponseDTO dto = new GradeResponseDTO();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getCutId()).isNull();
        assertThat(dto.getActivityName()).isNull();
        assertThat(dto.getGradeValue()).isNull();
        assertThat(dto.getPercentage()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // When
        GradeResponseDTO dto = new GradeResponseDTO(1L, 2L, "Partial Exam", 4.0, 30.0);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutId()).isEqualTo(2L);
        assertThat(dto.getActivityName()).isEqualTo("Partial Exam");
        assertThat(dto.getGradeValue()).isEqualTo(4.0);
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        GradeResponseDTO dto = new GradeResponseDTO();

        // When
        dto.setId(5L);

        // Then
        assertThat(dto.getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should set and get cutId")
    void shouldSetAndGetCutId() {
        // Given
        GradeResponseDTO dto = new GradeResponseDTO();

        // When
        dto.setCutId(3L);

        // Then
        assertThat(dto.getCutId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should set and get activityName")
    void shouldSetAndGetActivityName() {
        // Given
        GradeResponseDTO dto = new GradeResponseDTO();

        // When
        dto.setActivityName("Final Exam");

        // Then
        assertThat(dto.getActivityName()).isEqualTo("Final Exam");
    }

    @Test
    @DisplayName("Should set and get gradeValue")
    void shouldSetAndGetGradeValue() {
        // Given
        GradeResponseDTO dto = new GradeResponseDTO();

        // When
        dto.setGradeValue(3.8);

        // Then
        assertThat(dto.getGradeValue()).isEqualTo(3.8);
    }

    @Test
    @DisplayName("Should set and get percentage")
    void shouldSetAndGetPercentage() {
        // Given
        GradeResponseDTO dto = new GradeResponseDTO();

        // When
        dto.setPercentage(40.0);

        // Then
        assertThat(dto.getPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should handle null gradeValue")
    void shouldHandleNullGradeValue() {
        // Given
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(null)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutId()).isEqualTo(2L);
        assertThat(dto.getActivityName()).isEqualTo("Partial Exam");
        assertThat(dto.getGradeValue()).isNull();
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle zero gradeValue")
    void shouldHandleZeroGradeValue() {
        // Given
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(0.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutId()).isEqualTo(2L);
        assertThat(dto.getActivityName()).isEqualTo("Partial Exam");
        assertThat(dto.getGradeValue()).isEqualTo(0.0);
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle maximum gradeValue")
    void shouldHandleMaximumGradeValue() {
        // Given
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(5.0)
            .percentage(30.0)
            .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCutId()).isEqualTo(2L);
        assertThat(dto.getActivityName()).isEqualTo("Partial Exam");
        assertThat(dto.getGradeValue()).isEqualTo(5.0);
        assertThat(dto.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        GradeResponseDTO dto1 = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        GradeResponseDTO dto2 = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto1 = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        GradeResponseDTO dto2 = GradeResponseDTO.builder()
            .id(2L)
            .cutId(3L)
            .activityName("Final Exam")
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
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto1 = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        GradeResponseDTO dto2 = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
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
        GradeResponseDTO dto = GradeResponseDTO.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Partial Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("1");
        assertThat(result).contains("2");
        assertThat(result).contains("Partial Exam");
        assertThat(result).contains("4.0");
        assertThat(result).contains("30.0");
    }

    @Test
    void equalsWhenCutIdDiffers() {
        GradeResponseDTO a = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(4.0).percentage(30.0).build();
        GradeResponseDTO b = GradeResponseDTO.builder().id(1L).cutId(2L).activityName("A").gradeValue(4.0).percentage(30.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenActivityNameDiffers() {
        GradeResponseDTO a = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(4.0).percentage(30.0).build();
        GradeResponseDTO b = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("B").gradeValue(4.0).percentage(30.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenGradeValueDiffers() {
        GradeResponseDTO a = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(4.0).percentage(30.0).build();
        GradeResponseDTO b = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(5.0).percentage(30.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenPercentageDiffers() {
        GradeResponseDTO a = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(4.0).percentage(30.0).build();
        GradeResponseDTO b = GradeResponseDTO.builder().id(1L).cutId(1L).activityName("A").gradeValue(4.0).percentage(40.0).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new GradeResponseDTO()).isEqualTo(new GradeResponseDTO());
    }

    @Test
    void equalsWithNullIdVsNonNull() {
        GradeResponseDTO withNull = new GradeResponseDTO();
        GradeResponseDTO withValue = GradeResponseDTO.builder().id(1L).build();
        assertThat(withNull).isNotEqualTo(withValue);
        assertThat(withValue).isNotEqualTo(withNull);
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new GradeResponseDTO().hashCode()).isEqualTo(new GradeResponseDTO().hashCode());
    }
}
