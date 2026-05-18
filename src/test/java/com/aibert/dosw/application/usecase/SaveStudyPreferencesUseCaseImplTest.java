package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.StudyMethod;
import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.domain.model.StudyTime;
import com.aibert.dosw.domain.ports.out.StudyPreferencesRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaveStudyPreferencesUseCaseImpl Tests")
class SaveStudyPreferencesUseCaseImplTest {

    @Mock
    private StudyPreferencesRepositoryPort preferencesRepository;

    @InjectMocks
    private SaveStudyPreferencesUseCaseImpl useCase;

    private static final String STUDENT_ID = "student123";

    private StudyPreferences buildInput(StudyTime time, StudyMethod method, String location) {
        return StudyPreferences.builder()
                .studentId(STUDENT_ID)
                .preferredStudyTime(time)
                .preferredStudyMethod(method)
                .preferredStudyLocation(location)
                .build();
    }

    // ─── Create (no existing record) ─────────────────────────────────────────

    @Test
    @DisplayName("Should create preferences with null id when no record exists")
    void shouldCreatePreferencesWhenNoneExist() {
        StudyPreferences input = buildInput(StudyTime.MORNING, StudyMethod.INDIVIDUAL, "Biblioteca");
        StudyPreferences saved = StudyPreferences.builder()
                .id(1L).studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.MORNING)
                .preferredStudyMethod(StudyMethod.INDIVIDUAL)
                .preferredStudyLocation("Biblioteca")
                .build();

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenReturn(saved);

        StudyPreferences result = useCase.save(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(preferencesRepository).save(argThat(p -> p.getId() == null));
    }

    @Test
    @DisplayName("Should persist all fields on creation")
    void shouldPersistAllFieldsOnCreation() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(
                buildInput(StudyTime.MORNING, StudyMethod.INDIVIDUAL, "Biblioteca"));

        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals(StudyTime.MORNING, result.getPreferredStudyTime());
        assertEquals(StudyMethod.INDIVIDUAL, result.getPreferredStudyMethod());
        assertEquals("Biblioteca", result.getPreferredStudyLocation());
    }

    // ─── Update (existing record) ─────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences preserving their id")
    void shouldUpdateExistingPreferencesPreservingId() {
        StudyPreferences existing = StudyPreferences.builder()
                .id(42L).studentId(STUDENT_ID)
                .preferredStudyTime(StudyTime.AFTERNOON)
                .preferredStudyMethod(StudyMethod.GROUP)
                .preferredStudyLocation("Casa")
                .build();

        StudyPreferences input = buildInput(StudyTime.NIGHT, StudyMethod.MIXED, "Cafetería");

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(existing));
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(input);

        assertEquals(42L, result.getId());
        assertEquals(StudyTime.NIGHT, result.getPreferredStudyTime());
        assertEquals(StudyMethod.MIXED, result.getPreferredStudyMethod());
        assertEquals("Cafetería", result.getPreferredStudyLocation());
    }

    @Test
    @DisplayName("Should call repository save exactly once")
    void shouldCallRepositorySaveExactlyOnce() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(StudyTime.MORNING, StudyMethod.INDIVIDUAL, "Casa"));

        verify(preferencesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should call findByStudentId to check for existing record")
    void shouldCheckForExistingRecord() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput(StudyTime.MORNING, StudyMethod.INDIVIDUAL, "Casa"));

        verify(preferencesRepository, times(1)).findByStudentId(STUDENT_ID);
    }

    // ─── Null fields (all optional) ───────────────────────────────────────────

    @Test
    @DisplayName("Should save with all preference fields null")
    void shouldSaveWithAllFieldsNull() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(buildInput(null, null, null));

        assertNotNull(result);
        assertEquals(STUDENT_ID, result.getStudentId());
        assertNull(result.getPreferredStudyTime());
        assertNull(result.getPreferredStudyMethod());
        assertNull(result.getPreferredStudyLocation());
    }

    @Test
    @DisplayName("Should save with only some fields provided")
    void shouldSaveWithPartialFields() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(buildInput(StudyTime.EVENING, null, "Casa"));

        assertEquals(StudyTime.EVENING, result.getPreferredStudyTime());
        assertNull(result.getPreferredStudyMethod());
    }

    // ─── All StudyTime + StudyMethod combinations ─────────────────────────────

    @Test
    @DisplayName("Should accept all valid StudyTime values")
    void shouldAcceptAllStudyTimeValues() {
        for (StudyTime time : StudyTime.values()) {
            StudyPreferences input = buildInput(time, StudyMethod.INDIVIDUAL, "Casa");

            when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
            when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            StudyPreferences result = useCase.save(input);
            assertEquals(time, result.getPreferredStudyTime());
        }
    }

    @Test
    @DisplayName("Should accept all valid StudyMethod values")
    void shouldAcceptAllStudyMethodValues() {
        for (StudyMethod method : StudyMethod.values()) {
            StudyPreferences input = buildInput(StudyTime.MORNING, method, "Casa");

            when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
            when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            StudyPreferences result = useCase.save(input);
            assertEquals(method, result.getPreferredStudyMethod());
        }
    }
}
