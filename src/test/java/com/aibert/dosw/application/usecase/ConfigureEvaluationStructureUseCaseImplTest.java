package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.EvaluationStructureLockedException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigureEvaluationStructureUseCaseImpl Tests")
class ConfigureEvaluationStructureUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private ConfigureEvaluationStructureUseCaseImpl configureEvaluationStructureUseCase;

    private Subject testSubject;
    private List<EvaluationCut> validEvaluationCuts;
    private List<EvaluationCut> newEvaluationCuts;

    @BeforeEach
    void setUp() {
        validEvaluationCuts = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(30.0)
                .grade(null)
                .build()
        );
        
        newEvaluationCuts = List.of(
            EvaluationCut.builder()
                .id(null)
                .cutName("New Corte 1")
                .cutPercentage(50.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(null)
                .cutName("New Corte 2")
                .cutPercentage(50.0)
                .grade(null)
                .build()
        );
        
        testSubject = Subject.builder()
            .id(1L)
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(validEvaluationCuts)
            .build();
    }

    @Test
    @DisplayName("Should configure evaluation structure successfully with valid data")
    void shouldConfigureEvaluationStructureSuccessfully() {
        // Given
        Subject updatedSubject = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            newEvaluationCuts
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("New Corte 1", result.get(0).getCutName());
        assertEquals("New Corte 2", result.get(1).getCutName());
        assertEquals(50.0, result.get(0).getCutPercentage());
        assertEquals(50.0, result.get(1).getCutPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when subject not found")
    void shouldThrowSubjectNotFoundExceptionWhenSubjectNotFound() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw EvaluationStructureLockedException when subject has grades")
    void shouldThrowEvaluationStructureLockedExceptionWhenSubjectHasGrades() {
        // Given
        List<EvaluationCut> cutsWithGrades = List.of(
            EvaluationCut.builder()
                .id(1L)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(4.0)
                .build(),
            EvaluationCut.builder()
                .id(2L)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(3L)
                .cutName("Corte 3")
                .cutPercentage(30.0)
                .grade(null)
                .build()
        );
        
        Subject subjectWithGrades = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            cutsWithGrades
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subjectWithGrades));

        // When & Then
        EvaluationStructureLockedException exception = assertThrows(
            EvaluationStructureLockedException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when new cuts list is null")
    void shouldThrowInvalidEvaluationStructureExceptionWhenNewCutsListIsNull() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, null)
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when new cuts list is empty")
    void shouldThrowInvalidEvaluationStructureExceptionWhenNewCutsListIsEmpty() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, List.of())
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is less than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsLessThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 1")
                .cutPercentage(30.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 2")
                .cutPercentage(40.0)
                .grade(null)
                .build()
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, invalidCuts)
        );
        
        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("70.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is greater than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsGreaterThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 1")
                .cutPercentage(60.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 2")
                .cutPercentage(50.0)
                .grade(null)
                .build()
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> configureEvaluationStructureUseCase.configure(1L, invalidCuts)
        );
        
        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("110.0"));
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should configure successfully when percentage sum is exactly 100")
    void shouldConfigureSuccessfullyWhenPercentageSumIsExactly100() {
        // Given
        List<EvaluationCut> exactCuts = List.of(
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 1")
                .cutPercentage(50.0)
                .grade(null)
                .build(),
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte 2")
                .cutPercentage(50.0)
                .grade(null)
                .build()
        );
        
        Subject updatedSubject = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            exactCuts
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, exactCuts);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should configure successfully with single evaluation cut (100%)")
    void shouldConfigureSuccessfullyWithSingleEvaluationCut() {
        // Given
        List<EvaluationCut> singleCut = List.of(
            EvaluationCut.builder()
                .id(null)
                .cutName("Corte Unico")
                .cutPercentage(100.0)
                .grade(null)
                .build()
        );
        
        Subject updatedSubject = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            singleCut
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, singleCut);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Corte Unico", result.get(0).getCutName());
        assertEquals(100.0, result.get(0).getCutPercentage());
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should get structure successfully")
    void shouldGetStructureSuccessfully() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        List<EvaluationCut> result = configureEvaluationStructureUseCase.getStructure(1L);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Corte 1", result.get(0).getCutName());
        assertEquals("Corte 2", result.get(1).getCutName());
        assertEquals("Corte 3", result.get(2).getCutName());
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw SubjectNotFoundException when getting structure for non-existent subject")
    void shouldThrowSubjectNotFoundExceptionWhenGettingStructureForNonExistentSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        SubjectNotFoundException exception = assertThrows(
            SubjectNotFoundException.class,
            () -> configureEvaluationStructureUseCase.getStructure(1L)
        );
        
        assertTrue(exception.getMessage().contains("1"));
        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should preserve subject details when configuring structure")
    void shouldPreserveSubjectDetailsWhenConfiguringStructure() {
        // Given
        Subject updatedSubject = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            newEvaluationCuts
        );
        
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        // When
        List<EvaluationCut> result = configureEvaluationStructureUseCase.configure(1L, newEvaluationCuts);

        // Then
        verify(subjectRepository, times(1)).save(argThat(subject -> 
            subject.getId().equals(1L) &&
            subject.getStudentId().equals("student123") &&
            subject.getSubjectName().equals("Mathematics") &&
            subject.getCredits().equals(4) &&
            subject.getTeacherName().equals("Dr. Smith") &&
            subject.getSemester().equals("2025-1") &&
            subject.getEvaluationCuts().equals(newEvaluationCuts)
        ));
    }
}
