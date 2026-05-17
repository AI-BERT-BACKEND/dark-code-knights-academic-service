package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotOwnedException;
import com.aibert.dosw.domain.ports.in.DeleteSubjectUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteSubjectUseCaseImpl implements DeleteSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public void delete(Long id, String studentId) {
        subjectRepository.findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new SubjectNotOwnedException(id));
        subjectRepository.deleteById(id);
    }
}
