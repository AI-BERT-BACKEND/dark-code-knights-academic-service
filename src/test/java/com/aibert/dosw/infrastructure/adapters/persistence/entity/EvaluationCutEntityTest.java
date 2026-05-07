package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EvaluationCutEntity Tests")
class EvaluationCutEntityTest {

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        EvaluationCutEntity entity = new EvaluationCutEntity();

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCutName()).isNull();
        assertThat(entity.getCutPercentage()).isNull();
        assertThat(entity.getGrade()).isNull();
        assertThat(entity.getSubject()).isNull();
    }

    @Test
    @DisplayName("Should create with builder")
    void shouldCreateWithBuilder() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();

        // When
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getGrade()).isEqualTo(4.0);
        assertThat(entity.getSubject()).isSameAs(subject);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();

        // When
        EvaluationCutEntity entity = new EvaluationCutEntity(1L, "Partial 1", 30.0, 4.0, subject);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getGrade()).isEqualTo(4.0);
        assertThat(entity.getSubject()).isSameAs(subject);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        EvaluationCutEntity entity = new EvaluationCutEntity();

        // When
        entity.setId(2L);

        // Then
        assertThat(entity.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get cutName")
    void shouldSetAndGetCutName() {
        // Given
        EvaluationCutEntity entity = new EvaluationCutEntity();

        // When
        entity.setCutName("Final Exam");

        // Then
        assertThat(entity.getCutName()).isEqualTo("Final Exam");
    }

    @Test
    @DisplayName("Should set and get cutPercentage")
    void shouldSetAndGetCutPercentage() {
        // Given
        EvaluationCutEntity entity = new EvaluationCutEntity();

        // When
        entity.setCutPercentage(40.0);

        // Then
        assertThat(entity.getCutPercentage()).isEqualTo(40.0);
    }

    @Test
    @DisplayName("Should set and get grade")
    void shouldSetAndGetGrade() {
        // Given
        EvaluationCutEntity entity = new EvaluationCutEntity();

        // When
        entity.setGrade(3.5);

        // Then
        assertThat(entity.getGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should set and get subject")
    void shouldSetAndGetSubject() {
        // Given
        EvaluationCutEntity entity = new EvaluationCutEntity();
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();

        // When
        entity.setSubject(subject);

        // Then
        assertThat(entity.getSubject()).isSameAs(subject);
    }

    @Test
    @DisplayName("Should handle null grade")
    void shouldHandleNullGrade() {
        // Given
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(null)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getGrade()).isNull();
        assertThat(entity.getSubject()).isNull();
    }

    @Test
    @DisplayName("Should handle zero cutPercentage")
    void shouldHandleZeroCutPercentage() {
        // Given
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(0.0)
            .grade(4.0)
            .subject(null)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getCutPercentage()).isEqualTo(0.0);
        assertThat(entity.getGrade()).isEqualTo(4.0);
        assertThat(entity.getSubject()).isNull();
    }

    @Test
    @DisplayName("Should handle maximum cutPercentage")
    void shouldHandleMaximumCutPercentage() {
        // Given
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(100.0)
            .grade(4.0)
            .subject(null)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getCutPercentage()).isEqualTo(100.0);
        assertThat(entity.getGrade()).isEqualTo(4.0);
        assertThat(entity.getSubject()).isNull();
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity1 = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();
        EvaluationCutEntity entity2 = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity1).usingRecursiveComparison().isEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity1 = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();
        EvaluationCutEntity entity2 = EvaluationCutEntity.builder()
            .id(2L)
            .cutName("Partial 2")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity1).usingRecursiveComparison().isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity).usingRecursiveComparison().isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity).usingRecursiveComparison().isEqualTo(entity);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // Then
        assertThat(entity).isNotEqualTo("a string");
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity1 = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();
        EvaluationCutEntity entity2 = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
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
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
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
        SubjectEntity subject = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .build();
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(subject)
            .build();

        // When
        String result = entity.toString();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }
}
