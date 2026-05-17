package com.aibert.dosw.infrastructure.adapters.persistence.repository;

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
@DisplayName("EvaluationCutJpaRepository Tests")
class EvaluationCutJpaRepositoryTest {

    @Autowired
    private EvaluationCutJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save evaluation cut")
    void shouldSaveEvaluationCut() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();

        // When
        EvaluationCutEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCutName()).isEqualTo("Partial 1");
        assertThat(saved.getCutPercentage()).isEqualTo(30.0);
        assertThat(saved.getGrade()).isEqualTo(4.0);
        assertThat(saved.getSubject()).isSameAs(savedSubject);
    }

    @Test
    @DisplayName("Should find evaluation cuts by subject id")
    void shouldFindEvaluationCutsBySubjectId() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity1 = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity entity2 = EvaluationCutEntity.builder()
            .cutName("Partial 2")
            .cutPercentage(30.0)
            .grade(3.5)
            .subject(savedSubject)
            .build();
        
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);

        // When
        List<EvaluationCutEntity> found = repository.findBySubjectId(savedSubject.getId());

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting("cutName").containsExactly("Partial 1", "Partial 2");
    }

    @Test
    @DisplayName("Should return empty list when subject id not found")
    void shouldReturnEmptyListWhenSubjectIdNotFound() {
        // When
        List<EvaluationCutEntity> found = repository.findBySubjectId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find evaluation cut by id")
    void shouldFindEvaluationCutById() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        
        EvaluationCutEntity saved = entityManager.persistAndFlush(entity);

        // When
        EvaluationCutEntity found = repository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getCutName()).isEqualTo("Partial 1");
        assertThat(found.getCutPercentage()).isEqualTo(30.0);
        assertThat(found.getGrade()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should return empty optional when evaluation cut id not found")
    void shouldReturnEmptyOptionalWhenEvaluationCutIdNotFound() {
        // When
        EvaluationCutEntity found = repository.findById(999L).orElse(null);

        // Then
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Should delete evaluation cut")
    void shouldDeleteEvaluationCut() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        
        EvaluationCutEntity saved = repository.save(entity);
        
        // When
        repository.delete(saved);
        
        // Then
        EvaluationCutEntity deleted = repository.findById(saved.getId()).orElse(null);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should count evaluation cuts")
    void shouldCountEvaluationCuts() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity1 = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        EvaluationCutEntity entity2 = EvaluationCutEntity.builder()
            .cutName("Partial 2")
            .cutPercentage(30.0)
            .grade(3.5)
            .subject(savedSubject)
            .build();
        
        repository.save(entity1);
        repository.save(entity2);
        
        // When
        long count = repository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return true when evaluation cut exists by id")
    void shouldReturnTrueWhenEvaluationCutExistsById() {
        // Given
        SubjectEntity subject = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .teacherName("Dr. Smith")
            .credits(4)
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .build();
        SubjectEntity savedSubject = entityManager.persistAndFlush(subject);
        
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(savedSubject)
            .build();
        
        EvaluationCutEntity saved = repository.save(entity);
        
        // When
        boolean exists = repository.existsById(saved.getId());
        
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when evaluation cut id not found")
    void shouldReturnFalseWhenEvaluationCutIdNotFound() {
        // When
        boolean exists = repository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
