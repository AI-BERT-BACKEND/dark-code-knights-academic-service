package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.application.dto.response.EvaluationCutResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AveragesResponseDTO Tests")
class AveragesResponseDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of(
            EvaluationCutResponseDTO.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build()
        );

        // When
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSemester()).isEqualTo("2025-1");
        assertThat(dto.getOverallAverage()).isEqualTo(4.0);
        assertThat(dto.getCuts()).hasSize(1);
        assertThat(dto.getCuts().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCuts().get(0).getCutPercentage()).isEqualTo(30.0);
        assertThat(dto.getCuts().get(0).getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        AveragesResponseDTO dto = new AveragesResponseDTO();

        // Then
        assertThat(dto.getSubjectId()).isNull();
        assertThat(dto.getSubjectName()).isNull();
        assertThat(dto.getSemester()).isNull();
        assertThat(dto.getOverallAverage()).isNull();
        assertThat(dto.getCuts()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of();

        // When
        AveragesResponseDTO dto = new AveragesResponseDTO(1L, "Mathematics", "2025-1", 4.0, cuts);

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSemester()).isEqualTo("2025-1");
        assertThat(dto.getOverallAverage()).isEqualTo(4.0);
        assertThat(dto.getCuts()).isSameAs(cuts);
    }

    @Test
    @DisplayName("Should set and get subjectId")
    void shouldSetAndGetSubjectId() {
        // Given
        AveragesResponseDTO dto = new AveragesResponseDTO();

        // When
        dto.setSubjectId(2L);

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get subjectName")
    void shouldSetAndGetSubjectName() {
        // Given
        AveragesResponseDTO dto = new AveragesResponseDTO();

        // When
        dto.setSubjectName("Physics");

        // Then
        assertThat(dto.getSubjectName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("Should set and get semester")
    void shouldSetAndGetSemester() {
        // Given
        AveragesResponseDTO dto = new AveragesResponseDTO();

        // When
        dto.setSemester("2025-2");

        // Then
        assertThat(dto.getSemester()).isEqualTo("2025-2");
    }

    @Test
    @DisplayName("Should set and get overallAverage")
    void shouldSetAndGetOverallAverage() {
        // Given
        AveragesResponseDTO dto = new AveragesResponseDTO();

        // When
        dto.setOverallAverage(3.5);

        // Then
        assertThat(dto.getOverallAverage()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get cuts")
    void shouldSetAndGetCuts() {
        // Given
        AveragesResponseDTO dto = new AveragesResponseDTO();
        List<EvaluationCutResponseDTO> cuts = List.of(
            EvaluationCutResponseDTO.builder()
                .id(1L)
                .cutName("Final")
                .cutPercentage(40.0)
                .grade(3.8)
                .build()
        );

        // When
        dto.setCuts(cuts);

        // Then
        assertThat(dto.getCuts()).hasSize(1);
        assertThat(dto.getCuts().get(0).getId()).isEqualTo(1L);
        assertThat(dto.getCuts().get(0).getCutName()).isEqualTo("Final");
        assertThat(dto.getCuts().get(0).getCutPercentage()).isEqualTo(40.0);
        assertThat(dto.getCuts().get(0).getGrade()).isEqualTo(3.8);
    }

    @Test
    @DisplayName("Should handle null overallAverage")
    void shouldHandleNullOverallAverage() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(null)
            .cuts(List.of())
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSemester()).isEqualTo("2025-1");
        assertThat(dto.getOverallAverage()).isNull();
        assertThat(dto.getCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty cuts list")
    void shouldHandleEmptyCutsList() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
            .build();

        // Then
        assertThat(dto.getSubjectId()).isEqualTo(1L);
        assertThat(dto.getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSemester()).isEqualTo("2025-1");
        assertThat(dto.getOverallAverage()).isEqualTo(4.0);
        assertThat(dto.getCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple cuts")
    void shouldHandleMultipleCuts() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of(
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

        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(3.75)
            .cuts(cuts)
            .build();

        // Then
        assertThat(dto.getCuts()).hasSize(2);
        assertThat(dto.getCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(dto.getCuts().get(1).getCutName()).isEqualTo("Partial 2");
        assertThat(dto.getCuts().get(0).getGrade()).isEqualTo(4.0);
        assertThat(dto.getCuts().get(1).getGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of();
        AveragesResponseDTO dto1 = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();
        AveragesResponseDTO dto2 = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of();
        AveragesResponseDTO dto1 = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();
        AveragesResponseDTO dto2 = AveragesResponseDTO.builder()
            .subjectId(2L)
            .subjectName("Physics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        List<EvaluationCutResponseDTO> cuts = List.of();
        AveragesResponseDTO dto1 = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();
        AveragesResponseDTO dto2 = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(cuts)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
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
        AveragesResponseDTO dto = AveragesResponseDTO.builder()
            .subjectId(1L)
            .subjectName("Mathematics")
            .semester("2025-1")
            .overallAverage(4.0)
            .cuts(List.of())
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("1");
        assertThat(result).contains("Mathematics");
        assertThat(result).contains("2025-1");
        assertThat(result).contains("4.0");
    }
}
