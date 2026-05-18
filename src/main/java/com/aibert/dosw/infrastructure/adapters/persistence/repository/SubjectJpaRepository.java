package com.aibert.dosw.infrastructure.adapters.persistence.repository;

import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectJpaRepository extends JpaRepository<SubjectEntity, Long> {

    List<SubjectEntity> findByStudentId(String studentId);

    boolean existsByStudentIdAndSubjectNameAndSemester(String studentId, String subjectName, String semester);

    Optional<SubjectEntity> findByIdAndStudentId(Long id, String studentId);
}
