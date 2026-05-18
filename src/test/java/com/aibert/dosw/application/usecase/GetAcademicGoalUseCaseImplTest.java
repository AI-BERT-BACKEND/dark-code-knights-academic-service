package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.AverageCalculator;
import com.aibert.dosw.domain.exceptions.GoalNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.domain.model.AcademicGoalProgress;
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
    private static final Long SUBJECT_ID = 1L;

    private AcademicGoal testGoal;

    @BeforeEach
    void setUp() {
        testGoal = AcademicGoal.builder()
                .id(10L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(4.0).build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Subject subjectWithCuts(List<EvaluationCut> cuts) {
        return Subject.builder()
                .id(SUBJECT_ID)
                .studentId(STUDENT_ID)
                .subjectName("Matemáticas")
                .credits(3)
                .teacherName("Prof. Test")
                .semester("2025-1")
                .schedule("LUNES 08:00-10:00")
                .evaluationCuts(cuts)
                .build();
    }

    // ─── getProgress ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return progress when goal and subject exist with no grades yet")
    void shouldReturnProgressWithNoGradesYet() {
        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(40.0).grade(null).build(),
                EvaluationCut.builder().id(2L).cutName("Corte 2").cutPercentage(60.0).grade(null).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(null);

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertEquals(10L, result.getGoalId());
        assertEquals(SUBJECT_ID, result.getSubjectId());
        assertEquals("Matemáticas", result.getSubjectName());
        assertEquals(4.0, result.getTargetGrade());
        assertNull(result.getCurrentAverage());
        // pendingPercentage = 100 → requiredGrade = (4.0×100) / 100 = 4.0
        assertNotNull(result.getRequiredGrade());
        assertEquals(4.0, result.getRequiredGrade());
        assertTrue(result.isAchievable());
    }

    @Test
    @DisplayName("Should return achievable=true when required grade is within [0,5]")
    void shouldReturnAchievableWhenRequiredGradeIsWithinRange() {
        // target=4.0, cut1 graded=4.0 (40%), cut2 pending (60%)
        // currentScore = 4.0×40 = 160, pendingPercentage = 60
        // requiredGrade = (4.0×100 - 160) / 60 = 240/60 = 4.0 ≤ 5.0
        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(40.0).grade(4.0).build(),
                EvaluationCut.builder().id(2L).cutName("Corte 2").cutPercentage(60.0).grade(null).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(1.6); // partial average

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertEquals(4.0, result.getRequiredGrade());
        assertTrue(result.isAchievable());
    }

    @Test
    @DisplayName("Should return achievable=false when required grade exceeds 5.0")
    void shouldReturnNotAchievableWhenRequiredGradeExceedsFive() {
        // target=4.5, cut1 graded=1.0 (50%), cut2 pending (50%)
        // currentScore=50, pendingPercentage=50
        // requiredGrade=(4.5×100 - 50)/50 = 400/50 = 8.0 > 5.0
        AcademicGoal highGoal = AcademicGoal.builder()
                .id(10L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(4.5).build();

        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(50.0).grade(1.0).build(),
                EvaluationCut.builder().id(2L).cutName("Corte 2").cutPercentage(50.0).grade(null).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(highGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(0.5);

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertEquals(8.0, result.getRequiredGrade());
        assertFalse(result.isAchievable());
    }

    @Test
    @DisplayName("Should return requiredGrade=0.0 when goal is already secured")
    void shouldClampRequiredGradeToZeroWhenGoalAlreadySecured() {
        // target=3.0, cut1 graded=5.0 (70%), cut2 pending (30%)
        // currentScore=350, pendingPercentage=30
        // raw = (3.0×100 - 350)/30 = (300-350)/30 = -50/30 → clamped to 0.0
        AcademicGoal easyGoal = AcademicGoal.builder()
                .id(10L).subjectId(SUBJECT_ID).studentId(STUDENT_ID).targetGrade(3.0).build();

        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(70.0).grade(5.0).build(),
                EvaluationCut.builder().id(2L).cutName("Corte 2").cutPercentage(30.0).grade(null).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(easyGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(3.5);

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertEquals(0.0, result.getRequiredGrade());
        assertTrue(result.isAchievable());
    }

    @Test
    @DisplayName("Should return null requiredGrade and achievable=true when all cuts graded and average meets target")
    void shouldReturnNullRequiredGradeWhenAllCutsGradedAndMeetTarget() {
        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(40.0).grade(4.0).build(),
                EvaluationCut.builder().id(2L).cutName("Corte 2").cutPercentage(60.0).grade(4.5).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(4.3);

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertNull(result.getRequiredGrade());
        assertTrue(result.isAchievable());
    }

    @Test
    @DisplayName("Should return achievable=false when all cuts graded and average is below target")
    void shouldReturnNotAchievableWhenAllCutsGradedBelowTarget() {
        Subject subject = subjectWithCuts(List.of(
                EvaluationCut.builder().id(1L).cutName("Corte 1").cutPercentage(100.0).grade(3.0).build()));

        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testGoal)); // target=4.0
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(subject));
        when(averageCalculator.calculateOverallAverage(subject.getEvaluationCuts()))
                .thenReturn(3.0);

        AcademicGoalProgress result = useCase.getProgress(SUBJECT_ID, STUDENT_ID);

        assertNull(result.getRequiredGrade());
        assertFalse(result.isAchievable());
    }

    // ─── getProgress error paths ──────────────────────────────────────────────

    @Test
    @DisplayName("Should throw GoalNotFoundException when no goal exists for subject")
    void shouldThrowGoalNotFoundWhenNoGoalExists() {
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class,
                () -> useCase.getProgress(SUBJECT_ID, STUDENT_ID));

        verify(subjectRepository, never()).findByIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundWhenSubjectMissing() {
        when(goalRepository.findBySubjectIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.of(testGoal));
        when(subjectRepository.findByIdAndStudentId(SUBJECT_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> useCase.getProgress(SUBJECT_ID, STUDENT_ID));
    }

    // ─── getAllProgress ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return empty list when student has no goals")
    void shouldReturnEmptyListWhenNoGoals() {
        when(goalRepository.findByStudentId(STUDENT_ID)).thenReturn(List.of());

        List<AcademicGoalProgress> result = useCase.getAllProgress(STUDENT_ID, "2025-1");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return progress for goals in the requested semester only")
    void shouldFilterGoalsBySemester() {
        AcademicGoal goal2025_1 = AcademicGoal.builder()
                .id(1L).subjectId(1L).studentId(STUDENT_ID).targetGrade(4.0).build();
        AcademicGoal goal2025_2 = AcademicGoal.builder()
                .id(2L).subjectId(2L).studentId(STUDENT_ID).targetGrade(3.5).build();

        Subject subject2025_1 = Subject.builder()
                .id(1L).studentId(STUDENT_ID).subjectName("Matemáticas")
                .credits(3).teacherName("Prof").semester("2025-1")
                .schedule("LUNES 08:00-10:00")
                .evaluationCuts(List.of(
                        EvaluationCut.builder().id(1L).cutName("C1").cutPercentage(100.0).grade(null).build()))
                .build();

        Subject subject2025_2 = Subject.builder()
                .id(2L).studentId(STUDENT_ID).subjectName("Física")
                .credits(3).teacherName("Prof").semester("2025-2")
                .schedule("MARTES 10:00-12:00")
                .evaluationCuts(List.of(
                        EvaluationCut.builder().id(2L).cutName("C1").cutPercentage(100.0).grade(null).build()))
                .build();

        when(goalRepository.findByStudentId(STUDENT_ID))
                .thenReturn(List.of(goal2025_1, goal2025_2));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject2025_1));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject2025_2));
        when(averageCalculator.calculateOverallAverage(any())).thenReturn(null);

        List<AcademicGoalProgress> result = useCase.getAllProgress(STUDENT_ID, "2025-1");

        assertEquals(1, result.size());
        assertEquals("Matemáticas", result.get(0).getSubjectName());
    }

    @Test
    @DisplayName("Should skip goal if subject no longer exists")
    void shouldSkipGoalIfSubjectNoLongerExists() {
        when(goalRepository.findByStudentId(STUDENT_ID))
                .thenReturn(List.of(testGoal));
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        List<AcademicGoalProgress> result = useCase.getAllProgress(STUDENT_ID, "2025-1");

        assertTrue(result.isEmpty());
    }
}
