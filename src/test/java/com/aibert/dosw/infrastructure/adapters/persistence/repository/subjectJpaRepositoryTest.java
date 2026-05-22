package com.aibert.dosw.infrastructure.adapters.persistence.repository;

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
@DisplayName("SubjectJpaRepository Tests")
class SubjectJpaRepositoryTest {

    @Autowired
    private SubjectJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save subject")
    void shouldSaveSubject() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();

        // When
        SubjectEntity saved = repository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudentId()).isEqualTo("student123");
        assertThat(saved.getSubjectName()).isEqualTo("Mathematics");
        assertThat(saved.getCredits()).isEqualTo(4);
        assertThat(saved.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(saved.getSemester()).isEqualTo("2025-1");
        assertThat(saved.getEvaluationCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should find subjects by student id")
    void shouldFindSubjectsByStudentId() {
        // Given
        SubjectEntity entity1 = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Physics")
            .credits(3)
            .teacherName("Dr. Johnson")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);

        // When
        List<SubjectEntity> found = repository.findByStudentId("student123");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting("subjectName").containsExactly("Mathematics", "Physics");
    }

    @Test
    @DisplayName("Should return empty list when student id not found")
    void shouldReturnEmptyListWhenStudentIdNotFound() {
        // When
        List<SubjectEntity> found = repository.findByStudentId("student999");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find subject by id")
    void shouldFindSubjectById() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        SubjectEntity saved = entityManager.persistAndFlush(entity);

        // When
        SubjectEntity found = repository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getStudentId()).isEqualTo("student123");
        assertThat(found.getSubjectName()).isEqualTo("Mathematics");
        assertThat(found.getCredits()).isEqualTo(4);
        assertThat(found.getTeacherName()).isEqualTo("Dr. Smith");
        assertThat(found.getSemester()).isEqualTo("2025-1");
        assertThat(found.getEvaluationCuts()).isEmpty();
    }

    @Test
    @DisplayName("Should return empty optional when subject id not found")
    void shouldReturnEmptyOptionalWhenSubjectIdNotFound() {
        // When
        SubjectEntity found = repository.findById(999L).orElse(null);

        // Then
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Should check if subject exists by unique constraints")
    void shouldCheckIfSubjectExistsByUniqueConstraints() {
        // Given
        SubjectEntity entity1 = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .studentId("student456")
            .subjectName("Physics")
            .credits(3)
            .teacherName("Dr. Johnson")
            .semester("2025-2")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);

        // When
        boolean exists = repository.existsByStudentIdAndSubjectNameAndSemester("student123", "Mathematics", "2025-1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when subject does not exist by unique constraints")
    void shouldReturnFalseWhenSubjectDoesNotExistByUniqueConstraints() {
        // When
        boolean exists = repository.existsByStudentIdAndSubjectNameAndSemester("student999", "NonExistent", "2025-1");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should delete subject")
    void shouldDeleteSubject() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        SubjectEntity saved = repository.save(entity);

        // When
        repository.delete(saved);

        // Then
        SubjectEntity deleted = repository.findById(saved.getId()).orElse(null);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should count subjects")
    void shouldCountSubjects() {
        // Given
        SubjectEntity entity1 = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        SubjectEntity entity2 = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Physics")
            .credits(3)
            .teacherName("Dr. Johnson")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        repository.save(entity1);
        repository.save(entity2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return true when subject exists by id")
    void shouldReturnTrueWhenSubjectExistsById() {
        // Given
        SubjectEntity entity = SubjectEntity.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .credits(4)
            .teacherName("Dr. Smith")
            .semester("2025-1")
            .schedule("Lunes 08:30 - 10:00")
            .evaluationCuts(List.of())
            .build();
        
        SubjectEntity saved = repository.save(entity);

        // When
        boolean exists = repository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when subject id not found")
    void shouldReturnFalseWhenSubjectIdNotFound() {
        // When
        boolean exists = repository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
