package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectNotOwnedException;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetSubjectsUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSubjectsUseCaseImpl implements GetSubjectsUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public List<Subject> getAllByStudent(String studentId) {
        return subjectRepository.findByStudentId(studentId);
    }

    @Override
    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));
    }

    @Override
    public Subject getByIdAndStudent(Long id, String studentId) {
        return subjectRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new SubjectNotOwnedException(id));
    }
}
