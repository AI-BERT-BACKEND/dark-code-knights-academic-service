package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private static final String GOAL_NAME = "Aprobar Cálculo";
    private static final Long SUBJECT_ID = 10L;
    private static final Double TARGET = 4.0;

    // ─── General goal (no subject) ────────────────────────────────────────────

    @Test
    @DisplayName("Should create a general goal (no subjectId) without touching subjectRepository")
    void shouldCreateGeneralGoalWithoutSubject() {
        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenAnswer(inv -> {
            AcademicGoal g = inv.getArgument(0);
            return AcademicGoal.builder().id(1L).studentId(g.getStudentId())
                    .goalName(g.getGoalName()).targetGrade(g.getTargetGrade()).build();
        });

        AcademicGoal result = useCase.set(STUDENT_ID, GOAL_NAME, TARGET, null);

        assertNotNull(result);
        assertEquals(GOAL_NAME, result.getGoalName());
        assertEquals(TARGET, result.getTargetGrade());
        assertNull(result.getSubjectId());
        verify(subjectRepository, never()).findByIdAndStudentId(any(), any());
    }

    // ─── Subject-linked goal ──────────────────────────────────────────────────

    @Test
    @DisplayName("Should create a subject-linked goal when subject belongs to student")
    void shouldCreateSubjectLinkedGoal() {
        Subject subject = Subject.builder().id(SUBJECT_ID).studentId(STUDENT_ID).build();

        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AcademicGoal result = useCase.set(STUDENT_ID, GOAL_NAME, TARGET, SUBJECT_ID);

        assertEquals(SUBJECT_ID, result.getSubjectId());
        assertEquals(GOAL_NAME, result.getGoalName());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not belong to student")
    void shouldThrowWhenSubjectNotOwnedByStudent() {
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> useCase.set(STUDENT_ID, GOAL_NAME, TARGET, SUBJECT_ID));

        verify(goalRepository, never()).save(any());
    }

    // ─── Upsert by (studentId, goalName) ─────────────────────────────────────

    @Test
    @DisplayName("Should create with null id when no existing goal for that name")
    void shouldCreateWithNullIdWhenNoExistingGoal() {
        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.set(STUDENT_ID, GOAL_NAME, TARGET, null);

        verify(goalRepository).save(argThat(g -> g.getId() == null));
    }

    @Test
    @DisplayName("Should reuse existing id when goal name already exists for student")
    void shouldReuseExistingIdWhenGoalExists() {
        AcademicGoal existing = AcademicGoal.builder()
                .id(42L).studentId(STUDENT_ID).goalName(GOAL_NAME).targetGrade(3.0).build();

        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.of(existing));
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AcademicGoal result = useCase.set(STUDENT_ID, GOAL_NAME, 4.5, null);

        assertEquals(42L, result.getId());
        assertEquals(4.5, result.getTargetGrade());
    }

    // ─── Field persistence ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should persist all fields correctly")
    void shouldPersistAllFields() {
        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AcademicGoal result = useCase.set(STUDENT_ID, GOAL_NAME, TARGET, null);

        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(GOAL_NAME, result.getGoalName());
        assertEquals(TARGET, result.getTargetGrade());
    }

    @Test
    @DisplayName("Should call goalRepository.save exactly once")
    void shouldCallSaveExactlyOnce() {
        when(goalRepository.findByStudentIdAndGoalName(STUDENT_ID, GOAL_NAME))
                .thenReturn(Optional.empty());
        when(goalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.set(STUDENT_ID, GOAL_NAME, TARGET, null);

        verify(goalRepository, times(1)).save(any());
    }
}
