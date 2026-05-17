package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSubjectUseCaseImpl Tests")
class CreateSubjectUseCaseImplTest {

    @Mock
    private SubjectRepositoryPort subjectRepository;

    @InjectMocks
    private CreateSubjectUseCaseImpl createSubjectUseCase;

    private Subject testSubject;
    private List<EvaluationCut> validEvaluationCuts;

    @BeforeEach
    void setUp() {
        validEvaluationCuts = List.of(
            EvaluationCut.builder().cutName("Corte 1").cutPercentage(30.0).build(),
            EvaluationCut.builder().cutName("Corte 2").cutPercentage(40.0).build(),
            EvaluationCut.builder().cutName("Corte 3").cutPercentage(30.0).build()
        );
        
        testSubject = Subject.builder()
            .studentId("student123")
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(validEvaluationCuts)
            .build();
    }

    @Test
    @DisplayName("Should create subject successfully with valid data")
    void shouldCreateSubjectSuccessfullyWithValidData() {
        // Given
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Mathematics"), eq("2025-1"))).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(testSubject);

        // When
        Subject result = createSubjectUseCase.create(testSubject);

        // Then
        assertNotNull(result);
        assertEquals("Mathematics", result.getSubjectName());
        assertEquals("student123", result.getStudentId());
        assertEquals("2025-1", result.getSemester());
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Mathematics", "2025-1");
        verify(subjectRepository, times(1)).save(testSubject);
    }

    @Test
    @DisplayName("Should throw DuplicateSubjectException when subject already exists")
    void shouldThrowDuplicateSubjectExceptionWhenSubjectAlreadyExists() {
        // Given
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Mathematics"), eq("2025-1"))).thenReturn(true);

        // When & Then
        DuplicateSubjectException exception = assertThrows(
            DuplicateSubjectException.class,
            () -> createSubjectUseCase.create(testSubject)
        );
        
        assertTrue(exception.getMessage().contains("Mathematics"));
        assertTrue(exception.getMessage().contains("2025-1"));
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Mathematics", "2025-1");
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is null")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsListIsNull() {
        // Given
        Subject subjectWithNullCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            null
        );

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithNullCuts)
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when evaluation cuts list is empty")
    void shouldThrowInvalidEvaluationStructureExceptionWhenEvaluationCutsListIsEmpty() {
        // Given
        Subject subjectWithEmptyCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            List.of()
        );

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithEmptyCuts)
        );
        
        assertEquals("La materia debe tener al menos un corte evaluativo", exception.getMessage());
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is less than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsLessThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            new EvaluationCut(null, "Corte 1", 30.0, null),
            new EvaluationCut(null, "Corte 2", 40.0, null)
        );
        Subject subjectWithInvalidCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            invalidCuts
        );

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithInvalidCuts)
        );
        
        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("70.0"));
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException when percentage sum is greater than 100")
    void shouldThrowInvalidEvaluationStructureExceptionWhenPercentageSumIsGreaterThan100() {
        // Given
        List<EvaluationCut> invalidCuts = List.of(
            new EvaluationCut(null, "Corte 1", 60.0, null),
            new EvaluationCut(null, "Corte 2", 50.0, null)
        );
        Subject subjectWithInvalidCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            invalidCuts
        );

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithInvalidCuts)
        );
        
        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        assertTrue(exception.getMessage().contains("110.0"));
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should create subject successfully when percentage sum is exactly 100")
    void shouldCreateSubjectSuccessfullyWhenPercentageSumIsExactly100() {
        // Given
        List<EvaluationCut> exactCuts = List.of(
            new EvaluationCut(null, "Corte 1", 50.0, null),
            new EvaluationCut(null, "Corte 2", 50.0, null)
        );
        Subject subjectWithExactCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            exactCuts
        );
        
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Mathematics"), eq("2025-1"))).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithExactCuts);

        // When
        Subject result = createSubjectUseCase.create(subjectWithExactCuts);

        // Then
        assertNotNull(result);
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Mathematics", "2025-1");
        verify(subjectRepository, times(1)).save(subjectWithExactCuts);
    }

    @Test
    @DisplayName("Should create subject successfully with single evaluation cut (100%)")
    void shouldCreateSubjectSuccessfullyWithSingleEvaluationCut() {
        // Given
        List<EvaluationCut> singleCut = List.of(
            new EvaluationCut(null, "Corte Unico", 100.0, null)
        );
        Subject subjectWithSingleCut = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            singleCut
        );
        
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Mathematics"), eq("2025-1"))).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithSingleCut);

        // When
        Subject result = createSubjectUseCase.create(subjectWithSingleCut);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getEvaluationCuts().size());
        assertEquals("Corte Unico", result.getEvaluationCuts().get(0).getCutName());
        assertEquals(100.0, result.getEvaluationCuts().get(0).getCutPercentage());
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Mathematics", "2025-1");
        verify(subjectRepository, times(1)).save(subjectWithSingleCut);
    }

    @Test
    @DisplayName("Should throw InvalidEvaluationStructureException with very close to 100 but not exactly")
    void shouldThrowInvalidEvaluationStructureExceptionWithVeryCloseTo100ButNotExactly() {
        // Given
        List<EvaluationCut> closeCuts = List.of(
            new EvaluationCut(null, "Corte 1", 99.99, null),
            new EvaluationCut(null, "Corte 2", 0.001, null)
        );
        Subject subjectWithCloseCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            closeCuts
        );

        // When & Then
        InvalidEvaluationStructureException exception = assertThrows(
            InvalidEvaluationStructureException.class,
            () -> createSubjectUseCase.create(subjectWithCloseCuts)
        );
        
        assertTrue(exception.getMessage().contains("suma de porcentajes de los cortes debe ser exactamente 100"));
        verify(subjectRepository, never()).existsByStudentIdAndSubjectNameAndSemester(any(), any(), any());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should handle decimal percentages correctly")
    void shouldHandleDecimalPercentagesCorrectly() {
        // Given
        List<EvaluationCut> decimalCuts = List.of(
            new EvaluationCut(null, "Corte 1", 33.33, null),
            new EvaluationCut(null, "Corte 2", 33.33, null),
            new EvaluationCut(null, "Corte 3", 33.34, null)
        );
        Subject subjectWithDecimalCuts = new Subject(
            testSubject.getId(),
            testSubject.getStudentId(),
            testSubject.getSubjectName(),
            testSubject.getCredits(),
            testSubject.getTeacherName(),
            testSubject.getSemester(),
            testSubject.getSchedule(),
            decimalCuts
        );
        
        when(subjectRepository.existsByStudentIdAndSubjectNameAndSemester(
            eq("student123"), eq("Mathematics"), eq("2025-1"))).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subjectWithDecimalCuts);

        // When
        Subject result = createSubjectUseCase.create(subjectWithDecimalCuts);

        // Then
        assertNotNull(result);
        verify(subjectRepository, times(1)).existsByStudentIdAndSubjectNameAndSemester(
            "student123", "Mathematics", "2025-1");
        verify(subjectRepository, times(1)).save(subjectWithDecimalCuts);
    }
}
