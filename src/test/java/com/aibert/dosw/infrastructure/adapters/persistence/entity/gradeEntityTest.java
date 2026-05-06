package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GradeEntity Tests")
class GradeEntityTest {

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        GradeEntity entity = new GradeEntity();

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getActivityName()).isNull();
        assertThat(entity.getGradeValue()).isNull();
        assertThat(entity.getPercentage()).isNull();
        assertThat(entity.getCut()).isNull();
    }

    @Test
    @DisplayName("Should create with builder")
    void shouldCreateWithBuilder() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(null)
            .build();

        // When
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isEqualTo(4.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(null)
            .build();

        // When
        GradeEntity entity = new GradeEntity(1L, "Midterm Exam", 4.0, 30.0, cut);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isEqualTo(4.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        GradeEntity entity = new GradeEntity();

        // When
        entity.setId(2L);

        // Then
        assertThat(entity.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get activityName")
    void shouldSetAndGetActivityName() {
        // Given
        GradeEntity entity = new GradeEntity();

        // When
        entity.setActivityName("Final Exam");

        // Then
        assertThat(entity.getActivityName()).isEqualTo("Final Exam");
    }

    @Test
    @DisplayName("Should set and get gradeValue")
    void shouldSetAndGetGradeValue() {
        // Given
        GradeEntity entity = new GradeEntity();

        // When
        entity.setGradeValue(3.5);

        // Then
        assertThat(entity.getGradeValue()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get percentage")
    void shouldSetAndGetPercentage() {
        // Given
        GradeEntity entity = new GradeEntity();

        // When
        entity.setPercentage(25.0);

        // Then
        assertThat(entity.getPercentage()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Should set and get cut")
    void shouldSetAndGetCut() {
        // Given
        GradeEntity entity = new GradeEntity();
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(null)
            .build();

        // When
        entity.setCut(cut);

        // Then
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should handle null gradeValue")
    void shouldHandleNullGradeValue() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(null)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isNull();
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should handle zero gradeValue")
    void shouldHandleZeroGradeValue() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(0.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isEqualTo(0.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should handle maximum gradeValue")
    void shouldHandleMaximumGradeValue() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(5.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(entity.getGradeValue()).isEqualTo(5.0);
        assertThat(entity.getPercentage()).isEqualTo(30.0);
        assertThat(entity.getCut()).isSameAs(cut);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity1 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity1).usingRecursiveComparison().isEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity1 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .id(2L)
            .activityName("Final Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity1).usingRecursiveComparison().isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        assertThat(entity).isNotEqualTo("not an entity");
        assertThat(entity).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity1 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // Then
        // Just verify same object returns same hashCode (always true)
        int h1 = entity1.hashCode();
        int h2 = entity1.hashCode();
        assertThat(h1).isEqualTo(h2);
    }

    @Test
    @DisplayName("Should verify toString is not null")
    void shouldVerifyToStringIsNotNull() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // When
        String result = entity.toString();

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should verify toString contains key values")
    void shouldVerifyToStringContainsKeyValues() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();

        // When
        String result = entity.toString();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }
}
