package com.aibert.dosw.application.usecase;

import com.aibert.dosw.application.service.ScheduleParser;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.ScheduleConflict;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DetectScheduleConflictsUseCaseImpl Tests")
class DetectScheduleConflictsUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    /**
     * Use the real ScheduleParser so overlap logic is exercised end-to-end
     * (it has no external dependencies).
     */
    private final ScheduleParser scheduleParser = new ScheduleParser();

    @InjectMocks
    private DetectScheduleConflictsUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";
    private static final String SEMESTER = "2025-1";

    @BeforeEach
    void injectRealParser() {
        useCase = new DetectScheduleConflictsUseCaseImpl(subjectRepository, scheduleParser);
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Subject subject(Long id, String name, String schedule) {
        return Subject.builder()
                .id(id)
                .studentId(STUDENT_ID)
                .subjectName(name)
                .credits(3)
                .teacherName("Prof. Test")
                .semester(SEMESTER)
                .schedule(schedule)
                .evaluationCuts(List.of(EvaluationCut.builder()
                        .id(1L).cutName("Corte 1").cutPercentage(100.0).build()))
                .build();
    }

    // ─── No conflicts ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return empty list when student has no subjects in semester")
    void shouldReturnEmptyListWhenNoSubjects() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of());

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertTrue(result.isEmpty());
        verify(subjectRepository, times(1)).findByStudentIdAndSemester(STUDENT_ID, SEMESTER);
    }

    @Test
    @DisplayName("Should return empty list when only one subject exists")
    void shouldReturnEmptyListForSingleSubject() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(subject(1L, "Matemáticas", "LUNES 08:00-10:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when subjects are on different days")
    void shouldReturnEmptyListForSubjectsOnDifferentDays() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "LUNES 08:00-10:00"),
                        subject(2L, "Física", "MARTES 08:00-10:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when subjects on same day have touching but non-overlapping slots")
    void shouldReturnEmptyListForTouchingSlots() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "LUNES 08:00-10:00"),
                        subject(2L, "Física", "LUNES 10:00-12:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when subjects have unparseable free-form schedules")
    void shouldReturnEmptyListForUnparseableSchedules() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "Lunes y miércoles 8 a 10"),
                        subject(2L, "Física", "Martes 2pm")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertTrue(result.isEmpty());
    }

    // ─── Conflicts detected ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should detect conflict when two subjects share overlapping time on the same day")
    void shouldDetectConflictForOverlappingSlots() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "LUNES 08:00-10:00"),
                        subject(2L, "Física", "LUNES 09:00-11:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertEquals(1, result.size());
        ScheduleConflict conflict = result.get(0);
        assertEquals(1L, conflict.getSubjectAId());
        assertEquals("Matemáticas", conflict.getSubjectAName());
        assertEquals(2L, conflict.getSubjectBId());
        assertEquals("Física", conflict.getSubjectBName());
        assertEquals("LUNES 08:00-10:00", conflict.getConflictingSlot());
    }

    @Test
    @DisplayName("Should detect conflict when one subject's slot is fully contained inside another")
    void shouldDetectConflictForContainedSlot() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "JUEVES 08:00-12:00"),
                        subject(2L, "Física", "JUEVES 09:00-11:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should detect multiple conflicts across three subjects")
    void shouldDetectMultipleConflictsAcrossThreeSubjects() {
        // Algebra conflicts with Biologia (LUNES) and Algebra conflicts with Calculo (MARTES)
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Algebra", "LUNES 08:00-10:00, MARTES 08:00-10:00"),
                        subject(2L, "Biologia", "LUNES 09:00-11:00"),
                        subject(3L, "Calculo", "MARTES 09:00-11:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should detect conflict when subjects overlap on one of multiple slots")
    void shouldDetectConflictOnOneOfMultipleSlots() {
        // Subject A: LUNES 08:00-10:00 and MIERCOLES 14:00-16:00
        // Subject B: MIERCOLES 15:00-17:00 (overlaps with A's Wednesday slot only)
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "Matemáticas", "LUNES 08:00-10:00, MIERCOLES 14:00-16:00"),
                        subject(2L, "Física", "MIERCOLES 15:00-17:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        assertEquals(1, result.size());
        assertEquals("MIERCOLES 14:00-16:00", result.get(0).getConflictingSlot());
    }

    @Test
    @DisplayName("Should not duplicate conflicts — each pair is evaluated once")
    void shouldNotDuplicateConflicts() {
        when(subjectRepository.findByStudentIdAndSemester(STUDENT_ID, SEMESTER))
                .thenReturn(List.of(
                        subject(1L, "A", "VIERNES 08:00-10:00"),
                        subject(2L, "B", "VIERNES 09:00-11:00")));

        List<ScheduleConflict> result = useCase.detect(STUDENT_ID, SEMESTER);

        // Only one conflict (A→B), not duplicated as B→A
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should call repository with exact student and semester parameters")
    void shouldCallRepositoryWithCorrectParameters() {
        when(subjectRepository.findByStudentIdAndSemester("studentX", "2026-2"))
                .thenReturn(List.of());

        useCase.detect("studentX", "2026-2");

        verify(subjectRepository, times(1)).findByStudentIdAndSemester("studentX", "2026-2");
        verify(subjectRepository, never()).findByStudentIdAndSemester(eq("studentX"), eq("2025-1"));
    }
}
