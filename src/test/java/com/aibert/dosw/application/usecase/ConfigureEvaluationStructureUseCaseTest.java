package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.EvaluationStructureLockedException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.out.SubjectRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigureEvaluationStructureUseCase Tests")
class ConfigureEvaluationStructureUseCaseTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private ConfigureEvaluationStructureUseCaseImpl configureEvaluationStructureUseCase;

    private Subject subject;
    private List<EvaluationCut> validEvaluationCuts;
    private List<EvaluationCut> newEvaluationCuts;

    @BeforeEach
    void setUp() {
        validEvaluationCuts = Arrays.asList(
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
        );

        subject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(validEvaluationCuts)
            .build();

        newEvaluationCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Nuevo Corte 1")
                .cutPercentage(30.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Nuevo Corte 2")
                .cutPercentage(70.0)
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        reset(subjectRepository);
    }

    @Test
    @DisplayName("Should configure evaluation structure successfully")
    void shouldConfigureEvaluationStructureSuccessfully() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .grade(null)
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));
        
        // Create the expected updated subject with new cut names
        Subject expectedUpdatedSubject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(newEvaluationCuts)
            .build();
        
        when(subjectRepository.save(any(Subject.class))).thenReturn(expectedUpdatedSubject);

        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nuevo Corte 1", result.get(0).getCutName());
        assertEquals("Nuevo Corte 2", result.get(1).getCutName());

        verify(subjectRepository).findById(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject does not exist")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectDoesNotExist() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw EvaluationStructureLockedException when subject has grades")
    void shouldThrowEvaluationStructureLockedExceptionWhenSubjectHasGrades() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        EvaluationStructureLockedException exception = assertThrows(
            EvaluationStructureLockedException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts)
        );

        assertTrue(exception.getMessage().contains("1"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is null")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsIsNull() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));

        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, null)
        );

        assertTrue(exception.getMessage().contains("debe tener al menos un corte evaluativo"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is empty")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsIsEmpty() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));

        Executable configureAction = () -> configureEvaluationStructureUseCase.configure(1L, Collections.emptyList());
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            configureAction
        );

        assertTrue(exception.getMessage().contains("debe tener al menos un corte evaluativo"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentages sum less than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentagesSumLessThan100() {
        // Setup subject without grades
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        // Invalid cuts that sum to 70.0
        List<EvaluationCut> invalidCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(40.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(30.0)
                .build()
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));

        // This should throw InvalidEvaluationStructureException
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, invalidCuts)
        );

        assertTrue(exception.getMessage().contains("La suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("70.0"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentages sum more than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentagesSumMoreThan100() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        List<EvaluationCut> invalidCuts = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte 1")
                .cutPercentage(60.0)
                .build(),
            EvaluationCut.builder()
                .cutName("Corte 2")
                .cutPercentage(50.0)
                .build()
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));

        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, invalidCuts)
        );

        assertTrue(exception.getMessage().contains("La suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("110.0"));

        verify(subjectRepository).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should get evaluation structure successfully")
    void shouldGetEvaluationStructureSuccessfully() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        List<EvaluationCut> result = configureEvaluationStructureUseCase.getStructure(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Corte 1", result.get(0).getCutName());
        assertEquals("Corte 2", result.get(1).getCutName());

        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting structure for non-existent subject")
    void shouldThrowSubjectNotFoundExceptionWhenGettingStructureForNonExistentSubject() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> configureEvaluationStructureUseCase.getStructure(999L)
        );

        assertTrue(exception.getMessage().contains("999"));

        verify(subjectRepository).findById(999L);
    }

    @Test
    @DisplayName("Should configure evaluation structure with single cut")
    void shouldConfigureEvaluationStructureWithSingleCut() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        List<EvaluationCut> singleCut = Arrays.asList(
            EvaluationCut.builder()
                .cutName("Corte Único")
                .cutPercentage(100.0)
                .build()
        );

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));
        
        // Create the expected updated subject with single cut name
        Subject expectedUpdatedSubject = Subject.builder()
            .id(1L)
            .studentId("student-test")
            .subjectName("Cálculo Diferencial")
            .credits(4)
            .teacherName("Prof. Ramírez")
            .semester("2025-1")
            .evaluationCuts(singleCut)
            .build();
        
        when(subjectRepository.save(any(Subject.class))).thenReturn(expectedUpdatedSubject);

        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, singleCut);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Corte Único", result.get(0).getCutName());
        assertEquals(100.0, result.get(0).getCutPercentage());

        verify(subjectRepository).findById(1L);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should preserve subject information when updating evaluation structure")
    void shouldPreserveSubjectInformationWhenUpdatingEvaluationStructure() {
        Subject subjectWithoutGrades = Subject.builder()
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
                    .build()
            ))
            .build();

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithoutGrades));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithoutGrades);

        configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts);

        verify(subjectRepository).save(argThat(savedSubject -> {
            return savedSubject.getId().equals(1L) &&
                   savedSubject.getStudentId().equals("student-test") &&
                   savedSubject.getSubjectName().equals("Cálculo Diferencial") &&
                   savedSubject.getCredits().equals(4) &&
                   savedSubject.getTeacherName().equals("Prof. Ramírez") &&
                   savedSubject.getSemester().equals("2025-1");
        }));
    }
}
