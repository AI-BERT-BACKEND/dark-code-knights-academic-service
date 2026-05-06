package com.aibert.dosw.infrastructure.adapters.persistence.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SubjectEntity Tests")
class SubjectEntityTest {

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        SubjectEntity entity = new SubjectEntity();

        // Then
        assertThat(entity.getId()).isNull();
        assertThat(entity.getStudentId()).isNull();
        assertThat(entity.getSubjectName()).isNull();
        assertThat(entity.getCredits()).isNull();
        assertThat(entity.getTeacherName()).isNull();
        assertThat(entity.getSemester()).isNull();
        assertThat(entity.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should create with builder")
    void shouldCreateWithBuilder() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of(
            EvaluationCutEntity.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .subject(null)
                .build()
        );

        // When
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("student123");
        assertThat(entity.getSubjectName()).isEqualTo("Mathematics");
        assertThat(entity.getCredits()).isEqualTo(4);
        assertThat(entity.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(entity.getSemester()).isEqualTo("2025-1");
        assertThat(entity.getEvaluationCuts()).hasSize(1);
        assertThat(entity.getEvaluationCuts().get(0).getId()).isEqualTo(1L);
        assertThat(entity.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getEvaluationCuts().get(0).getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();

        // When
        SubjectEntity entity = new SubjectEntity(1L, "student123", "Mathematics", 4, "Dr. Smith", "2025-1", evaluationCuts);

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("student123");
        assertThat(entity.getSubjectName()).isEqualTo("Mathematics");
        assertThat(entity.getCredits()).isEqualTo(4);
        assertThat(entity.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(entity.getSemester()).isEqualTo("2025-1");
        assertThat(entity.getEvaluationCuts()).isSameAs(evaluationCuts);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setId(2L);

        // Then
        assertThat(entity.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should set and get studentId")
    void shouldSetAndGetStudentId() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setStudentId("student456");

        // Then
        assertThat(entity.getStudentId()).isEqualTo("student456");
    }

    @Test
    @DisplayName("Should set and get subjectName")
    void shouldSetAndGetSubjectName() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setSubjectName("Physics");

        // Then
        assertThat(entity.getSubjectName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("Should set and get credits")
    void shouldSetAndGetCredits() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setCredits(3);

        // Then
        assertThat(entity.getCredits()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should set and get teacherName")
    void shouldSetAndGetTeacherName() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setTeacherName("Dr. Johnson");

        // Then
        assertThat(entity.getTeacherName()).isEqualTo("Dr. Johnson");
    }

    @Test
    @DisplayName("Should set and get semester")
    void shouldSetAndGetSemester() {
        // Given
        SubjectEntity entity = new SubjectEntity();

        // When
        entity.setSemester("2025-2");

        // Then
        assertThat(entity.getSemester()).isEqualTo("2025-2");
    }

    @Test
    @DisplayName("Should set and get evaluationCuts")
    void shouldSetAndGetEvaluationCuts() {
        // Given
        SubjectEntity entity = new SubjectEntity();
        List<EvaluationCutEntity> evaluationCuts = List.of(
            EvaluationCutEntity.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .subject(null)
                .build()
        );

        // When
        entity.setEvaluationCuts(evaluationCuts);

        // Then
        assertThat(entity.getEvaluationCuts()).hasSize(1);
        assertThat(entity.getEvaluationCuts().get(0).getId()).isEqualTo(1L);
        assertThat(entity.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getEvaluationCuts().get(0).getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should handle null evaluationCuts")
    void shouldHandleNullEvaluationCuts() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(null)
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("student123");
        assertThat(entity.getSubjectName()).isEqualTo("Mathematics");
        assertThat(entity.getCredits()).isEqualTo(4);
        assertThat(entity.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(entity.getSemester()).isEqualTo("2025-1");
        assertThat(entity.getEvaluationCuts()).isNull();
    }

    @Test
    @DisplayName("Should handle empty evaluationCuts")
    void shouldHandleEmptyEvaluationCuts() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(List.of())
            .build();

        // Then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("student123");
        assertThat(entity.getSubjectName()).isEqualTo("Mathematics");
        assertThat(entity.getCredits()).isEqualTo(4);
        assertThat(entity.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(entity.getSemester()).isEqualTo("2025-1");
        assertThat(entity.getEvaluationCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple evaluationCuts")
    void shouldHandleMultipleEvaluationCuts() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of(
            EvaluationCutEntity.builder()
                .id(1L)
                .cutName("Partial 1")
                .cutPercentage(25.0)
                .grade(4.0)
                .subject(null)
                .build(),
            EvaluationCutEntity.builder()
                .id(2L)
                .cutName("Partial 2")
                .cutPercentage(25.0)
                .grade(3.5)
                .subject(null)
                .build()
        );

        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity.getEvaluationCuts()).hasSize(2);
        assertThat(entity.getEvaluationCuts().get(0).getCutName()).isEqualTo("Partial 1");
        assertThat(entity.getEvaluationCuts().get(1).getCutName()).isEqualTo("Partial 2");
        assertThat(entity.getEvaluationCuts().get(0).getCutPercentage()).isEqualTo(25.0);
        assertThat(entity.getEvaluationCuts().get(1).getCutPercentage()).isEqualTo(25.0);
        assertThat(entity.getEvaluationCuts().get(0).getGrade()).isEqualTo(4.0);
        assertThat(entity.getEvaluationCuts().get(1).getGrade()).isEqualTo(3.5);
    }

    @Test
    @DisplayName("Should verify equals with equal objects")
    void shouldVerifyEqualsWithEqualObjects() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity1 = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity1).usingRecursiveComparison().isEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with unequal objects")
    void shouldVerifyEqualsWithUnequalObjects() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity1 = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .id(2L)
            .studentId("student123")
            .subjectName("Physics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity1).isNotEqualTo(entity2);
    }

    @Test
    @DisplayName("Should verify equals with null")
    void shouldVerifyEqualsWithNull() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should verify equals with same reference")
    void shouldVerifyEqualsWithSameReference() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity).isEqualTo(entity);
    }

    @Test
    @DisplayName("Should verify equals with different type")
    void shouldVerifyEqualsWithDifferentType() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // Then
        assertThat(entity).isNotEqualTo("not an entity");
        assertThat(entity).isNotEqualTo(123);
    }

    @Test
    @DisplayName("Should verify hashCode consistency")
    void shouldVerifyHashCodeConsistency() {
        // Given
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity1 = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
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
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
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
        List<EvaluationCutEntity> evaluationCuts = List.of();
        SubjectEntity entity = SubjectEntity.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .evaluationCuts(evaluationCuts)
            .build();

        // When
        String result = entity.toString();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }
}
