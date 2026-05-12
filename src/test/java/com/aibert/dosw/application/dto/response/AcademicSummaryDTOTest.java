package com.aibert.dosw.application.dto.response;

import com.aibert.dosw.application.dto.response.AveragesResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AcademicSummaryDTO Tests")
class AcademicSummaryDTOTest {

    @Test
    @DisplayName("Should create with builder pattern")
    void shouldCreateWithBuilderPattern() {
        // Given
        List<AveragesResponseDTO> subjects = List.of(
            AveragesResponseDTO.builder()
                .subjectId(1L)
                .subjectName("Mathematics")
                .semester("2025-1")
                .overallAverage(4.0)
                .cuts(List.of())
                .build()
        );

        // When
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.8)
            .subjects(subjects)
            .build();

        // Then
        assertThat(dto.getStudentId()).isEqualTo("student123");
        assertThat(dto.getAcademicGpa()).isEqualTo(3.8);
        assertThat(dto.getSubjects()).hasSize(1);
        assertThat(dto.getSubjects().get(0).getSubjectId()).isEqualTo(1L);
        assertThat(dto.getSubjects().get(0).getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSubjects().get(0).getOverallAverage()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        AcademicSummaryDTO dto = new AcademicSummaryDTO();

        // Then
        assertThat(dto.getStudentId()).isNull();
        assertThat(dto.getAcademicGpa()).isNull();
        assertThat(dto.getSubjects()).isNull();
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        List<AveragesResponseDTO> subjects = List.of();

        // When
        AcademicSummaryDTO dto = new AcademicSummaryDTO("student456", 3.5, subjects);

        // Then
        assertThat(dto.getStudentId()).isEqualTo("student456");
        assertThat(dto.getAcademicGpa()).isEqualTo(3.5);
        assertThat(dto.getSubjects()).isSameAs(subjects);
    }

    @Test
    @DisplayName("Should set and get studentId")
    void shouldSetAndGetStudentId() {
        // Given
        AcademicSummaryDTO dto = new AcademicSummaryDTO();

        // When
        dto.setStudentId("newStudent");

        // Then
        assertThat(dto.getStudentId()).isEqualTo("newStudent");
    }

    @Test
    @DisplayName("Should set and get academicGpa")
    void shouldSetAndGetAcademicGpa() {
        // Given
        AcademicSummaryDTO dto = new AcademicSummaryDTO();

        // When
        dto.setAcademicGpa(4.2);

        // Then
        assertThat(dto.getAcademicGpa()).isEqualTo(4.2);
    }

    @Test
    @DisplayName("Should set and get subjects")
    void shouldSetAndGetSubjects() {
        // Given
        AcademicSummaryDTO dto = new AcademicSummaryDTO();
        List<AveragesResponseDTO> subjects = List.of(
            AveragesResponseDTO.builder()
                .subjectId(2L)
                .subjectName("Physics")
                .build()
        );

        // When
        dto.setSubjects(subjects);

        // Then
        assertThat(dto.getSubjects()).hasSize(1);
        assertThat(dto.getSubjects().get(0).getSubjectId()).isEqualTo(2L);
        assertThat(dto.getSubjects().get(0).getSubjectName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("Should handle null academicGpa")
    void shouldHandleNullAcademicGpa() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(null)
            .subjects(List.of())
            .build();

        // Then
        assertThat(dto.getStudentId()).isEqualTo("student123");
        assertThat(dto.getAcademicGpa()).isNull();
        assertThat(dto.getSubjects()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty subjects list")
    void shouldHandleEmptySubjectsList() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.0)
            .subjects(List.of())
            .build();

        // Then
        assertThat(dto.getStudentId()).isEqualTo("student123");
        assertThat(dto.getAcademicGpa()).isEqualTo(3.0);
        assertThat(dto.getSubjects()).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple subjects")
    void shouldHandleMultipleSubjects() {
        // Given
        List<AveragesResponseDTO> subjects = List.of(
            AveragesResponseDTO.builder()
                .subjectId(1L)
                .subjectName("Mathematics")
                .overallAverage(4.0)
                .build(),
            AveragesResponseDTO.builder()
                .subjectId(2L)
                .subjectName("Physics")
                .overallAverage(3.5)
                .build()
        );

        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.75)
            .subjects(subjects)
            .build();

        // Then
        assertThat(dto.getStudentId()).isEqualTo("student123");
        assertThat(dto.getAcademicGpa()).isEqualTo(3.75);
        assertThat(dto.getSubjects()).hasSize(2);
        assertThat(dto.getSubjects().get(0).getSubjectName()).isEqualTo("Mathematics");
        assertThat(dto.getSubjects().get(1).getSubjectName()).isEqualTo("Physics");
        assertThat(dto.getSubjects().get(0).getOverallAverage()).isEqualTo(4.0);
        assertThat(dto.getSubjects().get(1).getOverallAverage()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        List<AveragesResponseDTO> subjects = List.of();
        AcademicSummaryDTO dto1 = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();
        AcademicSummaryDTO dto2 = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();

        // Then
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        List<AveragesResponseDTO> subjects = List.of();
        AcademicSummaryDTO dto1 = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();
        AcademicSummaryDTO dto2 = AcademicSummaryDTO.builder()
            .studentId("student456")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();

        // Then
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(List.of())
            .build();

        // Then
        assertThat(dto).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(List.of())
            .build();

        // Then
        assertThat(dto).isNotEqualTo("not a dto");
        assertThat(dto).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        List<AveragesResponseDTO> subjects = List.of();
        AcademicSummaryDTO dto1 = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();
        AcademicSummaryDTO dto2 = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(subjects)
            .build();

        // Then
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(List.of())
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
        AcademicSummaryDTO dto = AcademicSummaryDTO.builder()
            .studentId("student123")
            .academicGpa(3.5)
            .subjects(List.of())
            .build();

        // When
        String result = dto.toString();

        // Then
        assertThat(result).contains("student123");
        assertThat(result).contains("3.5");
    }

    @Test
    void equalsWhenAcademicGpaDiffers() {
        AcademicSummaryDTO a = AcademicSummaryDTO.builder().studentId("s1").academicGpa(3.5).subjects(List.of()).build();
        AcademicSummaryDTO b = AcademicSummaryDTO.builder().studentId("s1").academicGpa(4.0).subjects(List.of()).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWhenSubjectsDiffer() {
        List<AveragesResponseDTO> s1 = List.of(AveragesResponseDTO.builder().subjectId(1L).build());
        List<AveragesResponseDTO> s2 = List.of(AveragesResponseDTO.builder().subjectId(2L).build());
        AcademicSummaryDTO a = AcademicSummaryDTO.builder().studentId("s1").academicGpa(3.5).subjects(s1).build();
        AcademicSummaryDTO b = AcademicSummaryDTO.builder().studentId("s1").academicGpa(3.5).subjects(s2).build();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalsWithAllNullFields() {
        assertThat(new AcademicSummaryDTO()).isEqualTo(new AcademicSummaryDTO());
    }

    @Test
    void equalsWithNullStudentIdVsNonNull() {
        AcademicSummaryDTO withNull = new AcademicSummaryDTO();
        AcademicSummaryDTO withValue = AcademicSummaryDTO.builder().studentId("s1").build();
        assertThat(withNull).isNotEqualTo(withValue);
        assertThat(withValue).isNotEqualTo(withNull);
    }

    @Test
    void hashCodeWithNullFields() {
        assertThat(new AcademicSummaryDTO().hashCode()).isEqualTo(new AcademicSummaryDTO().hashCode());
    }
}
