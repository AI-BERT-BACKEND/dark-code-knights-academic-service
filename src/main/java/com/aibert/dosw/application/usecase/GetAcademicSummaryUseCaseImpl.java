package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.GetAcademicSummaryUseCase;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAcademicSummaryUseCaseImpl implements GetAcademicSummaryUseCase {

    private final SubjectRepositoryPort subjectRepository;

    @Override
    public List<Subject> getSummary(String studentId) {
        return subjectRepository.findByStudentId(studentId);
    }
}
