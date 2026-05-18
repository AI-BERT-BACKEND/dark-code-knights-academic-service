package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SetAcademicGoalUseCaseImpl Tests")
class SetAcademicGoalUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private AcademicGoalRepositoryPort goalRepository;

    @InjectMocks
    private SetAcademicGoalUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";
    private static final Long SUBJECT_ID = 1L;

    private Subject testSubject;

    @BeforeEach
    void setUp() {
        testSubject = Subject.builder()
                .id(SUBJECT_ID)
                .studentId(STUDENT_ID)
                .subjectName("Matemáticas")
                .credits(3)
                .teacherName("Prof. Test")
                .semester("2025-1")
                .schedule("LUNES 08:00-10:00")
                .evaluationCuts(List.of(EvaluationCut.builder()
                        .id(1L).cutName("Corte 1").cutPercentage(100.0).build()))
                .build();
    }

    // ─── Create (no existing goal) ────────────────────────────────────────────

    @Test
    @DisplayName("Should create a new goal when none exists for the subject")
    void shouldCreateGoalWhenNoneExists() {
        AcademicGoal saved = AcademicGoal.builder()
                .id(10L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(4.0).build();

        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testSubject));
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any(AcademicGoal.class))).thenReturn(saved);

        AcademicGoal result = useCase.set(SUBJECT_ID, STUDENT_ID, 4.0);

        assertEquals(10L, result.getId());
        assertEquals(4.0, result.getTargetGrade());
        verify(goalRepository, times(1)).save(any(AcademicGoal.class));
    }

    @Test
    @DisplayName("Should save goal with null id so JPA generates it on first creation")
    void shouldSaveWithNullIdOnFirstCreation() {
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testSubject));
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any(AcademicGoal.class))).thenAnswer(inv -> inv.getArgument(0));

        AcademicGoal result = useCase.set(SUBJECT_ID, STUDENT_ID, 3.5);

        assertNull(result.getId());
        assertEquals(3.5, result.getTargetGrade());
    }

    // ─── Update (existing goal) ───────────────────────────────────────────────

    @Test
    @DisplayName("Should update the existing goal preserving its id")
    void shouldUpdateExistingGoalPreservingId() {
        AcademicGoal existing = AcademicGoal.builder()
                .id(99L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(3.0).build();
        AcademicGoal updated = AcademicGoal.builder()
                .id(99L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(4.5).build();

        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testSubject));
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(existing));
        when(goalRepository.save(any(AcademicGoal.class))).thenReturn(updated);

        AcademicGoal result = useCase.set(SUBJECT_ID, STUDENT_ID, 4.5);

        assertEquals(99L, result.getId());
        assertEquals(4.5, result.getTargetGrade());
        verify(goalRepository, times(1)).save(argThat(g -> g.getId().equals(99L)));
    }

    // ─── Error paths ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowWhenSubjectNotFound() {
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> useCase.set(SUBJECT_ID, STUDENT_ID, 4.0));

        verify(goalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject belongs to another student")
    void shouldThrowWhenSubjectBelongsToAnotherStudent() {
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, "other-student"))
                .thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> useCase.set(SUBJECT_ID, "other-student", 4.0));

        verify(goalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should validate subject before checking existing goal")
    void shouldValidateSubjectBeforeCheckingExistingGoal() {
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> useCase.set(SUBJECT_ID, STUDENT_ID, 4.0));

        verify(goalRepository, never()).findBySubjectIdAndStudentId(any(), any());
    }

    // ─── Boundary grades ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Should accept targetGrade of 0.0")
    void shouldAcceptMinimumTargetGrade() {
        AcademicGoal saved = AcademicGoal.builder()
                .id(1L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(0.0).build();

        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testSubject));
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenReturn(saved);

        AcademicGoal result = useCase.set(SUBJECT_ID, STUDENT_ID, 0.0);

        assertEquals(0.0, result.getTargetGrade());
    }

    @Test
    @DisplayName("Should accept targetGrade of 5.0")
    void shouldAcceptMaximumTargetGrade() {
        AcademicGoal saved = AcademicGoal.builder()
                .id(1L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(5.0).build();

        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testSubject));
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenReturn(saved);

        AcademicGoal result = useCase.set(SUBJECT_ID, STUDENT_ID, 5.0);

        assertEquals(5.0, result.getTargetGrade());
    }
}
