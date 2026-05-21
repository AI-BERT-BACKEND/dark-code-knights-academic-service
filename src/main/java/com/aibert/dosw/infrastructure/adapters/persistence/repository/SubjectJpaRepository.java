package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectJpaRepository extends JpaRepository<SubjectEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"evaluationCuts"})
    Optional<SubjectEntity> findById(Long id);

    @EntityGraph(attributePaths = {"evaluationCuts"})
    List<SubjectEntity> findByStudentId(String studentId);

    boolean existsByStudentIdAndSubjectNameAndSemester(String studentId, String subjectName, String semester);

    @EntityGraph(attributePaths = {"evaluationCuts"})
    Optional<SubjectEntity> findByIdAndStudentId(Long id, String studentId);

    @EntityGraph(attributePaths = {"evaluationCuts"})
    Optional<SubjectEntity> findByExternalId(String externalId);
}
