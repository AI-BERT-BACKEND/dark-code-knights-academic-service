package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetSubjectsUseCase Tests")
class GetSubjectsUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private GetSubjectsUseCaseImpl getSubjectsUseCase;

    private List<Subject> subjects;
    private Subject subject;

    @BeforeEach
    void setUp() {
        subjects = Arrays.asList(
            Subject.builder()
                .id(1L)
                .studentId("student-test")
                .subjectName("Cálculo Diferencial")
                .credits(4)
                .teacherName("Prof. Ramírez")
                .semester("2025-1")
                .evaluationCuts(Arrays.asList(
                    EvaluationCut.builder()
                        .id(1L)
                        .cutName("Corte 1")
                        .cutPercentage(40.0)
                        .grade(null)
                        .build(),
                    EvaluationCut.builder()
                        .id(2L)
                        .cutName("Corte 2")
                        .cutPercentage(60.0)
                        .grade(3.5)
                        .build()
                ))
                .build(),
            Subject.builder()
                .id(2L)
                .studentId("student-test")
                .subjectName("Física General")
                .credits(3)
                .teacherName("Prof. Torres")
                .semester("2025-1")
                .evaluationCuts(Arrays.asList(
                    EvaluationCut.builder()
                        .id(3L)
                        .cutName("Corte 1")
                        .cutPercentage(50.0)
                        .grade(null)
                        .build(),
                    EvaluationCut.builder()
                        .id(4L)
                        .cutName("Corte 2")
                        .cutPercentage(50.0)
                        .grade(null)
                        .build()
                ))
                .build()
        );

        subject = subjects.get(0);
    }

    @Test
    @DisplayName("Should get all subjects by student ID successfully")
    void shouldGetAllSubjectsByStudentIdSuccessfully() {
        when(subjectRepository.findByStudentId("student-test")).thenReturn(subjects);

        List<Subject> result = getSubjectsUseCase.getAllByStudent("student-test");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cálculo Diferencial", result.get(0).getSubjectName());
        assertEquals("Física General", result.get(1).getSubjectName());
        assertEquals("student-test", result.get(0).getStudentId());
        assertEquals("student-test", result.get(1).getStudentId());

        verify(subjectRepository).findByStudentId("student-test");
    }

    @Test
    @DisplayName("Should return empty list when student has no subjects")
    void shouldReturnEmptyListWhenStudentHasNoSubjects() {
        when(subjectRepository.findByStudentId("new-student")).thenReturn(Collections.emptyList());

        List<Subject> result = getSubjectsUseCase.getAllByStudent("new-student");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(subjectRepository).findByStudentId("new-student");
    }

    @Test
    @DisplayName("Should get subject by ID successfully")
    void shouldGetSubjectByIdSuccessfully() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        Subject result = getSubjectsUseCase.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cálculo Diferencial", result.getSubjectName());
        assertEquals("student-test", result.getStudentId());
        assertEquals(4, result.getCredits());
        assertEquals("Prof. Ramírez", result.getTeacherName());
        assertEquals("2025-1", result.getSemester());
        assertEquals(2, result.getEvaluationCuts().size());

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not exist")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectDoesNotExist() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> getSubjectsUseCase.getById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));

        verify(subjectRepository).findById(999L);
    }

    @Test
    @DisplayName("Should handle subjects with different semesters")
    void shouldHandleSubjectsWithDifferentSemesters() {
        List<Subject> multiSemesterSubjects = Arrays.asList(
            Subject.builder()
                .id(1L)
                .studentId("student-test")
                .subjectName("Cálculo Diferencial")
                .credits(4)
                .teacherName("Prof. Ramírez")
                .semester("2025-1")
                .evaluationCuts(Collections.emptyList())
                .build(),
            Subject.builder()
                .id(2L)
                .studentId("student-test")
                .subjectName("Álgebra Lineal")
                .credits(3)
                .teacherName("Prof. García")
                .semester("2024-2")
                .evaluationCuts(Collections.emptyList())
                .build()
        );

        when(subjectRepository.findByStudentId("student-test")).thenReturn(multiSemesterSubjects);

        List<Subject> result = getSubjectsUseCase.getAllByStudent("student-test");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("2025-1", result.get(0).getSemester());
        assertEquals("2024-2", result.get(1).getSemester());

        verify(subjectRepository).findByStudentId("student-test");
    }

    @Test
    @DisplayName("Should handle subjects with different students")
    void shouldHandleSubjectsWithDifferentStudents() {
        when(subjectRepository.findByStudentId("student-1")).thenReturn(Arrays.asList(subjects.get(0)));
        when(subjectRepository.findByStudentId("student-2")).thenReturn(Arrays.asList(subjects.get(1)));

        List<Subject> result1 = getSubjectsUseCase.getAllByStudent("student-1");
        List<Subject> result2 = getSubjectsUseCase.getAllByStudent("student-2");

        assertNotNull(result1);
        assertEquals(1, result1.size());
        assertEquals("Cálculo Diferencial", result1.get(0).getSubjectName());

        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals("Física General", result2.get(0).getSubjectName());

        verify(subjectRepository).findByStudentId("student-1");
        verify(subjectRepository).findByStudentId("student-2");
    }

    @Test
    @DisplayName("Should handle subject with no evaluation cuts")
    void shouldHandleSubjectWithNoEvaluationCuts() {
        Subject subjectWithoutCuts = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(2)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(Collections.emptyList())
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutCuts));

        Subject result = getSubjectsUseCase.getById(1L);

        assertNotNull(result);
        assertEquals("Materia Simple", result.getSubjectName());
        assertTrue(result.getEvaluationCuts().isEmpty());

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle subject with single evaluation cut")
    void shouldHandleSubjectWithSingleEvaluationCut() {
        Subject subjectWithSingleCut = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Materia Simple")
            .credits(2)
            .teacherName("Prof. Simple")
            .semester("2025-1")
            .evaluationCuts(Arrays.asList(
                EvaluationCut.builder()
                    .id(1L)
                    .cutName("Corte Único")
                    .cutPercentage(100.0)
                    .grade(null)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithSingleCut));

        Subject result = getSubjectsUseCase.getById(1L);

        assertNotNull(result);
        assertEquals("Materia Simple", result.getSubjectName());
        assertEquals(1, result.getEvaluationCuts().size());
        assertEquals("Corte Único", result.getEvaluationCuts().get(0).getCutName());
        assertEquals(100.0, result.getEvaluationCuts().get(0).getCutPercentage());

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should preserve all subject fields when retrieving")
    void shouldPreserveAllSubjectFieldsWhenRetrieving() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        Subject result = getSubjectsUseCase.getById(1L);

        assertNotNull(result);
        assertEquals(subject.getId(), result.getId());
        assertEquals(subject.getStudentId(), result.getStudentId());
        assertEquals(subject.getSubjectName(), result.getSubjectName());
        assertEquals(subject.getCredits(), result.getCredits());
        assertEquals(subject.getTeacherName(), result.getTeacherName());
        assertEquals(subject.getSemester(), result.getSemester());
        assertEquals(subject.getEvaluationCuts().size(), result.getEvaluationCuts().size());

        verify(subjectRepository).findById(1L);
    }
}
