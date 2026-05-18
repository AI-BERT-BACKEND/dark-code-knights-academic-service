package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepositoryPort {

    Subject save(Subject subject);

    Optional<Subject> findById(Long id);

    List<Subject> findByStudentId(String studentId);

    boolean existsByStudentIdAndSubjectNameAndSemester(String studentId, String subjectName, String semester);

    Optional<Subject> findByIdAndStudentId(Long id, String studentId);

    void deleteById(Long id);

    List<Subject> findByStudentIdAndSemester(String studentId, String semester);
}
