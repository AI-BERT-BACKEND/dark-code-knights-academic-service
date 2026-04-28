package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.ports.in.DeleteSubjectUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteSubjectUseCaseImpl implements DeleteSubjectUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public void delete(Long id) {
        subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));
        subjectRepository.deleteById(id);
    }
}
