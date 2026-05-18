package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.GoalNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.AcademicGoalProgress;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.AcademicGoalRepositoryPort;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAcademicGoalUseCaseImpl Tests")
class GetAcademicGoalUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @Mock
    private AcademicGoalRepositoryPort goalRepository;

    @Mock
    private AverageCalculator averageCalculator;

    @InjectMocks
    private GetAcademicGoalUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";
    private static final Long GOAL_ID = 1L;
    private static final Long SUBJECT_ID = 10L;

    private AcademicGoal buildGoalWithSubject() {
        return AcademicGoal.builder()
                .id(GOAL_ID).studentId(STUDENT_ID)
                .goalName("Aprobar Cálculo").targetGrade(4.0)
                .subjectId(SUBJECT_ID).build();
    }

    private AcademicGoal buildGeneralGoal() {
        return AcademicGoal.builder()
                .id(GOAL_ID).studentId(STUDENT_ID)
                .goalName("Meta general").targetGrade(3.5)
                .subjectId(null).build();
    }

    private Subject buildSubject(List<EvaluationCut> cuts) {
        return Subject.builder()
                .id(SUBJECT_ID).studentId(STUDENT_ID)
                .subjectName("Cálculo").semester("2025-1")
                .evaluationCuts(cuts).build();
    }

    // ─── getById — happy path ─────────────────────────────────────────────────

    @Test
    @DisplayName("Should return progress for subject-linked goal")
    void shouldReturnProgressForSubjectLinkedGoal() {
        EvaluationCut graded = EvaluationCut.builder()
                .id(1L).cutName("Corte 1").cutPercentage(50.0).grade(4.0).build();
        EvaluationCut pending = EvaluationCut.builder()
                .id(2L).cutName("Corte 2").cutPercentage(50.0).grade(null).build();

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(buildGoalWithSubject()));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(buildSubject(List.of(graded, pending))));
        when(averageCalculator.calculateOverallAverage(List.of(graded, pending))).thenReturn(2.0);

        AcademicGoalProgress result = useCase.getById(GOAL_ID, STUDENT_ID);

        assertNotNull(result);
        assertEquals(GOAL_ID, result.getGoalId());
        assertEquals("Aprobar Cálculo", result.getGoalName());
        assertEquals("Cálculo", result.getSubjectName());
        assertEquals(2.0, result.getCurrentAverage());
    }

    @Test
    @DisplayName("Should return basic progress for general goal (no subject)")
    void shouldReturnBasicProgressForGeneralGoal() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(buildGeneralGoal()));

        AcademicGoalProgress result = useCase.getById(GOAL_ID, STUDENT_ID);

        assertEquals("Meta general", result.getGoalName());
        assertNull(result.getSubjectName());
        assertNull(result.getCurrentAverage());
        assertFalse(result.isAchievable());
    }

    // ─── getById — error paths ────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw GoalNotFoundException when goal does not exist")
    void shouldThrowWhenGoalDoesNotExist() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class,
                () -> useCase.getById(GOAL_ID, STUDENT_ID));
    }

    @Test
    @DisplayName("Should throw GoalNotFoundException when goal belongs to different student")
    void shouldThrowWhenGoalBelongsToDifferentStudent() {
        AcademicGoal otherStudentGoal = AcademicGoal.builder()
                .id(GOAL_ID).studentId("other-student")
                .goalName("Meta ajena").targetGrade(3.0).build();

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(otherStudentGoal));

        assertThrows(GoalNotFoundException.class,
                () -> useCase.getById(GOAL_ID, STUDENT_ID));
    }

    // ─── isAchievable logic ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should set isAchievable=true when requiredGrade <= 5.0")
    void shouldSetAchievableWhenRequiredGradeIsReachable() {
        EvaluationCut pending = EvaluationCut.builder()
                .id(1L).cutPercentage(100.0).grade(null).build();

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(buildGoalWithSubject()));
        when(subjectRepository.findById(SUBJECT_ID))
                .thenReturn(Optional.of(buildSubject(List.of(pending))));
        when(averageCalculator.calculateOverallAverage(List.of(pending))).thenReturn(null);

        AcademicGoalProgress result = useCase.getById(GOAL_ID, STUDENT_ID);

        assertTrue(result.isAchievable());
        assertEquals(4.0, result.getRequiredGrade());
    }

    @Test
    @DisplayName("Should set isAchievable=false when requiredGrade > 5.0")
    void shouldSetNotAchievableWhenRequiredGradeExceedsMax() {
        EvaluationCut graded = EvaluationCut.builder()
                .id(1L).cutPercentage(80.0).grade(0.0).build();
        EvaluationCut pending = EvaluationCut.builder()
                .id(2L).cutPercentage(20.0).grade(null).build();

        AcademicGoal highTarget = AcademicGoal.builder()
                .id(GOAL_ID).studentId(STUDENT_ID).goalName("Meta alta")
                .targetGrade(5.0).subjectId(SUBJECT_ID).build();

        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(highTarget));
        when(subjectRepository.findById(SUBJECT_ID))
                .thenReturn(Optional.of(buildSubject(List.of(graded, pending))));
        when(averageCalculator.calculateOverallAverage(List.of(graded, pending))).thenReturn(0.0);

        AcademicGoalProgress result = useCase.getById(GOAL_ID, STUDENT_ID);

        assertFalse(result.isAchievable());
    }

    // ─── getAllProgress ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all goals filtered by semester")
    void shouldFilterGoalsBySemester() {
        AcademicGoal goal = buildGoalWithSubject();
        Subject subjectWrongSemester = Subject.builder()
                .id(SUBJECT_ID).studentId(STUDENT_ID)
                .subjectName("Cálculo").semester("2024-1")
                .evaluationCuts(List.of()).build();

        when(goalRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of(goal));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subjectWrongSemester));

        List<AcademicGoalProgress> result = useCase.getAllProgress(STUDENT_ID, "2025-1");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when student has no goals")
    void shouldReturnEmptyListWhenNoGoals() {
        when(goalRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of());

        List<AcademicGoalProgress> result = useCase.getAllProgress(STUDENT_ID, null);

        assertTrue(result.isEmpty());
    }
}
