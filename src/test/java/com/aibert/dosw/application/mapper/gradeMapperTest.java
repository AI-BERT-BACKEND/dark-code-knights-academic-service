package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.GradeRequestDTO;
import com.aibert.dosw.application.dto.request.UpdateGradeRequestDTO;
import com.aibert.dosw.application.dto.response.GradeResponseDTO;
import com.aibert.dosw.domain.model.Grade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GradeMapper Tests")
class GradeMapperTest {

    private final GradeMapper mapper = Mappers.getMapper(GradeMapper.class);

    @Test
    @DisplayName("Should map grade request DTO to domain")
    void shouldMapGradeRequestDtoToDomain() {
        // Given
        GradeRequestDTO dto = GradeRequestDTO.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        Grade domain = mapper.toDomain(dto);

        // Then
        assertThat(domain.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(domain.getGradeValue()).isEqualTo(4.0);
        assertThat(domain.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should map update grade request DTO to domain")
    void shouldMapUpdateGradeRequestDtoToDomain() {
        // Given
        UpdateGradeRequestDTO dto = UpdateGradeRequestDTO.builder()
            .activityName("Final Exam")
            .gradeValue(3.5)
            .percentage(40.0)
            .build();

        // When
        Grade domain = mapper.toDomain(dto);

        // Then
        assertThat(domain.getActivityName()).isEqualTo("Final Exam");
        assertThat(domain.getGradeValue()).isEqualTo(3.5);
        assertThat(domain.getPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should map grade to response DTO")
    void shouldMapGradeToResponseDTO() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        GradeResponseDTO result = mapper.toResponseDTO(grade);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCutId()).isEqualTo(2L);
        assertThat(result.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.getGradeValue()).isEqualTo(4.0);
        assertThat(result.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should map grades to response DTO list")
    void shouldMapGradesToResponseDTOList() {
        // Given
        List<Grade> grades = List.of(
            Grade.builder()
                .id(1L)
                .cutId(2L)
                .activityName("Midterm Exam")
                .gradeValue(4.0)
                .percentage(30.0)
                .build(),
            Grade.builder()
                .id(2L)
                .cutId(3L)
                .activityName("Final Exam")
                .gradeValue(3.5)
                .percentage(40.0)
                .build()
        );

        // When
        List<GradeResponseDTO> result = mapper.toResponseDTOList(grades);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCutId()).isEqualTo(2L);
        assertThat(result.get(0).getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.get(0).getGradeValue()).isEqualTo(4.0);
        assertThat(result.get(0).getPercentage()).isEqualTo(30.0);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getCutId()).isEqualTo(3L);
        assertThat(result.get(1).getActivityName()).isEqualTo("Final Exam");
        assertThat(result.get(1).getGradeValue()).isEqualTo(3.5);
        assertThat(result.get(1).getPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should map null grades to response DTO list")
    void shouldMapNullGradesToResponseDTOList() {
        // Given
        List<Grade> grades = null;

        // When
        List<GradeResponseDTO> result = mapper.toResponseDTOList(grades);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map empty grades to response DTO list")
    void shouldMapEmptyGradesToResponseDTOList() {
        // Given
        List<Grade> grades = List.of();

        // When
        List<GradeResponseDTO> result = mapper.toResponseDTOList(grades);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should map single grade to response DTO list")
    void shouldMapSingleGradeToResponseDTOList() {
        // Given
        List<Grade> grades = List.of(
            Grade.builder()
                .id(1L)
                .cutId(2L)
                .activityName("Midterm Exam")
                .gradeValue(4.0)
                .percentage(30.0)
                .build()
        );

        // When
        List<GradeResponseDTO> result = mapper.toResponseDTOList(grades);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCutId()).isEqualTo(2L);
        assertThat(result.get(0).getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.get(0).getGradeValue()).isEqualTo(4.0);
        assertThat(result.get(0).getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should map multiple grades to response DTO list")
    void shouldMapMultipleGradesToResponseDTOList() {
        // Given
        List<Grade> grades = List.of(
            Grade.builder()
                .id(1L)
                .cutId(2L)
                .activityName("Midterm Exam")
                .gradeValue(4.0)
                .percentage(25.0)
                .build(),
            Grade.builder()
                .id(2L)
                .cutId(3L)
                .activityName("Final Exam")
                .gradeValue(3.5)
                .percentage(25.0)
                .build()
        );

        // When
        List<GradeResponseDTO> result = mapper.toResponseDTOList(grades);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCutId()).isEqualTo(2L);
        assertThat(result.get(0).getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.get(0).getGradeValue()).isEqualTo(4.0);
        assertThat(result.get(0).getPercentage()).isEqualTo(25.0);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getCutId()).isEqualTo(3L);
        assertThat(result.get(1).getActivityName()).isEqualTo("Final Exam");
        assertThat(result.get(1).getGradeValue()).isEqualTo(3.5);
        assertThat(result.get(1).getPercentage()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Should handle null grade")
    void shouldHandleNullGrade() {
        // Given
        Grade grade = null;

        // When
        GradeResponseDTO result = mapper.toResponseDTO(grade);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle grade with zero grade value")
    void shouldHandleGradeWithZeroGradeValue() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Zero Grade Exam")
            .gradeValue(0.0)
            .percentage(30.0)
            .build();

        // When
        GradeResponseDTO result = mapper.toResponseDTO(grade);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCutId()).isEqualTo(2L);
        assertThat(result.getActivityName()).isEqualTo("Zero Grade Exam");
        assertThat(result.getGradeValue()).isEqualTo(0.0);
        assertThat(result.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle grade with maximum grade value")
    void shouldHandleGradeWithMaximumGradeValue() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Maximum Grade Exam")
            .gradeValue(5.0)
            .percentage(30.0)
            .build();

        // When
        GradeResponseDTO result = mapper.toResponseDTO(grade);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCutId()).isEqualTo(2L);
        assertThat(result.getActivityName()).isEqualTo("Maximum Grade Exam");
        assertThat(result.getGradeValue()).isEqualTo(5.0);
        assertThat(result.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle grade with boundary percentage")
    void shouldHandleGradeWithBoundaryPercentage() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Boundary Percentage Exam")
            .gradeValue(4.0)
            .percentage(0.1)
            .build();

        // When
        GradeResponseDTO result = mapper.toResponseDTO(grade);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCutId()).isEqualTo(2L);
        assertThat(result.getActivityName()).isEqualTo("Boundary Percentage Exam");
        assertThat(result.getGradeValue()).isEqualTo(4.0);
        assertThat(result.getPercentage()).isEqualTo(0.1);
    }
}
