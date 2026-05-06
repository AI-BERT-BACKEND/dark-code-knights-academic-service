package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GradePersistenceMapper Tests")
class GradePersistenceMapperTest {

    private final GradePersistenceMapper mapper = Mappers.getMapper(GradePersistenceMapper.class);

    @Test
    @DisplayName("Should map domain to entity")
    void shouldMapDomainToEntity() {
        // Given
        Grade domain = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When
        GradeEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isEqualTo(4.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isNull();
    }

    @Test
    @DisplayName("Should map entity to domain")
    void shouldMapEntityToDomain() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // When
        Grade domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getCutId()).isEqualTo(1L);
        assertThat(domain.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(domain.getGradeValue()).isEqualTo(4.0);
        assertThat(domain.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should map entity list to domain list")
    void shouldMapEntityListToDomainList() {
        // Given
        List<GradeEntity> entityList = List.of(
            GradeEntity.builder()
                .id(1L)
                .activityName("Midterm Exam")
                .gradeValue(4.0)
                .percentage(30.0)
                .cut(null)
                .build(),
            GradeEntity.builder()
                .id(2L)
                .activityName("Final Exam")
                .gradeValue(3.5)
                .percentage(40.0)
                .cut(null)
                .build()
        );

        // When
        List<Grade> domainListResult = mapper.toDomainList(entityList);

        // Then
        assertThat(domainListResult).hasSize(2);
        assertThat(domainListResult.get(0).getId()).isEqualTo(1L);
        assertThat(domainListResult.get(0).getActivityName()).isEqualTo("Midterm Exam");
        assertThat(domainListResult.get(0).getGradeValue()).isEqualTo(4.0);
        assertThat(domainListResult.get(0).getPercentage()).isEqualTo(30.0);
        assertThat(domainListResult.get(1).getId()).isEqualTo(2L);
        assertThat(domainListResult.get(1).getActivityName()).isEqualTo("Final Exam");
        assertThat(domainListResult.get(1).getGradeValue()).isEqualTo(3.5);
        assertThat(domainListResult.get(1).getPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should map null entity list to domain list")
    void shouldMapNullEntityListToDomainList() {
        // Given
        List<GradeEntity> entityList = null;

        // When
        List<Grade> domainListResult = mapper.toDomainList(entityList);

        // Then
        assertThat(domainListResult).isNull();
    }

    @Test
    @DisplayName("Should map empty entity list to domain list")
    void shouldMapEmptyEntityListToDomainList() {
        // Given
        List<GradeEntity> entityList = List.of();

        // When
        List<Grade> domainListResult = mapper.toDomainList(entityList);

        // Then
        assertThat(domainListResult).isEmpty();
    }

    @Test
    @DisplayName("Should map null entity to domain")
    void shouldMapNullEntityToDomain() {
        // Given
        GradeEntity entity = null;

        // When
        Grade domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("Should map entity with null cut to domain")
    void shouldMapEntityWithNullCutToDomain() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(2L)
            .cutName("Final Exam")
            .cutPercentage(40.0)
            .grade(3.5)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // When
        Grade domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(domain.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(domain.getGradeValue()).isEqualTo(4.0);
        assertThat(domain.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should map entity with cut to domain")
    void shouldMapEntityWithCutToDomain() {
        // Given
        EvaluationCutEntity cutEntity = EvaluationCutEntity.builder()
            .id(2L)
            .cutName("Partial 1")
            .cutPercentage(25.0)
            .grade(3.5)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cutEntity)
            .build();

        // When
        Grade domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(domain.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(domain.getGradeValue()).isEqualTo(4.0);
        assertThat(domain.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle zero grade values")
    void shouldHandleZeroGradeValues() {
        // Given
        Grade domain = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Zero Grade Exam")
            .gradeValue(0.0)
            .percentage(30.0)
            .build();

        // When
        GradeEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(entity.getActivityName()).isEqualTo("Zero Grade Exam");
        assertThat(entity.getGradeValue()).isEqualTo(0.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle maximum grade values")
    void shouldHandleMaximumGradeValues() {
        // Given
        Grade domain = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Maximum Grade Exam")
            .gradeValue(5.0)
            .percentage(30.0)
            .build();

        // When
        GradeEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(entity.getActivityName()).isEqualTo("Maximum Grade Exam");
        assertThat(entity.getGradeValue()).isEqualTo(5.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should handle boundary percentage values")
    void shouldHandleBoundaryPercentageValues() {
        // Given
        Grade domain = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Boundary Percentage Exam")
            .gradeValue(4.0)
            .percentage(0.1)
            .build();

        // When
        GradeEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(domain.getCutId()).isEqualTo(2L);
        assertThat(entity.getActivityName()).isEqualTo("Boundary Percentage Exam");
        assertThat(entity.getGradeValue()).isEqualTo(4.0);
        assertThat(entity.getPercentage()).isEqualTo(0.1);
    }
}
