package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("GradeJpaRepository Tests")
class GradeJpaRepositoryTest {

    @Autowired
    private GradeJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save grade")
    void shouldSaveGrade() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(null)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();

        // When
        GradeEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(saved.getGradeValue()).isEqualTo(4.0);
        assertThat(saved.getPercentage()).isEqualTo(30.0);
        assertThat(saved.getCut()).isSameAs(savedCut);
    }

    @Test
    @DisplayName("Should find grades by cut id")
    void shouldFindGradesByCutId() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity1 = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .activityName("Final Exam")
            .gradeValue(3.5)
            .percentage(40.0)
            .cut(savedCut)
            .build();
        
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);

        // When
        List<GradeEntity> found = repository.findByCutId(savedCut.getId());

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting("activityName").containsExactly("Midterm Exam", "Final Exam");
    }

    @Test
    @DisplayName("Should return empty list when cut id not found")
    void shouldReturnEmptyListWhenCutIdNotFound() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        entityManager.persistAndFlush(GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build());

        // When
        List<GradeEntity> found = repository.findByCutId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find grade by id")
    void shouldFindGradeById() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
                        .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity = GradeEntity.builder()
                        .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        
        GradeEntity saved = entityManager.persistAndFlush(entity);

        // When
        GradeEntity found = repository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(found.getGradeValue()).isEqualTo(4.0);
        assertThat(found.getPercentage()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Should return empty optional when grade id not found")
    void shouldReturnEmptyOptionalWhenGradeIdNotFound() {
        // When
        GradeEntity found = repository.findById(999L).orElse(null);

        // Then
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Should delete grade")
    void shouldDeleteGrade() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        
        GradeEntity saved = repository.save(entity);

        // When
        repository.delete(saved);

        // Then
        GradeEntity deleted = repository.findById(saved.getId()).orElse(null);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should count grades")
    void shouldCountGrades() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
                        .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity1 = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .activityName("Final Exam")
            .gradeValue(3.5)
            .percentage(40.0)
            .cut(savedCut)
            .build();
        
        repository.save(entity1);
        repository.save(entity2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return true when grade exists by id")
    void shouldReturnTrueWhenGradeExistsById() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        
        GradeEntity saved = repository.save(entity);

        // When
        boolean exists = repository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when grade id not found")
    void shouldReturnFalseWhenGradeIdNotFound() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
                        .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity savedCut = entityManager.persistAndFlush(cut);
        
        GradeEntity entity = GradeEntity.builder()
                        .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(savedCut)
            .build();
        
        entityManager.persistAndFlush(entity);

        // When
        boolean exists = repository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
