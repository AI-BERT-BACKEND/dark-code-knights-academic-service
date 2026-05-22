package com.aibert.dosw.application.usecase;

import com.aibert.dosw.domain.model.StudyPreferences;
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

    private StudyPreferences buildInput(String modality, String environment, String method) {
        return StudyPreferences.builder()
                .studentId(STUDENT_ID)
                .studyModality(modality)
                .studyEnvironment(environment)
                .studyMethod(method)
                .build();
    }

    // ─── Create (no existing record) ─────────────────────────────────────────

    @Test
    @DisplayName("Should create preferences with null id when no record exists")
    void shouldCreatePreferencesWhenNoneExist() {
        StudyPreferences input = buildInput("VISUAL", "BIBLIOTECA", "INDIVIDUAL");
        StudyPreferences saved = StudyPreferences.builder()
                .id(1L).studentId(STUDENT_ID)
                .studyModality("VISUAL").studyEnvironment("BIBLIOTECA").studyMethod("INDIVIDUAL")
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

        StudyPreferences result = useCase.save(buildInput("VISUAL", "BIBLIOTECA", "INDIVIDUAL"));

        assertEquals(STUDENT_ID, result.getStudentId());
        assertEquals("VISUAL", result.getStudyModality());
        assertEquals("BIBLIOTECA", result.getStudyEnvironment());
        assertEquals("INDIVIDUAL", result.getStudyMethod());
    }

    // ─── Update (existing record) ─────────────────────────────────────────────

    @Test
    @DisplayName("Should update existing preferences preserving their id")
    void shouldUpdateExistingPreferencesPreservingId() {
        StudyPreferences existing = StudyPreferences.builder()
                .id(42L).studentId(STUDENT_ID)
                .studyModality("AUDITIVO").studyEnvironment("CASA").studyMethod("GRUPO")
                .build();

        StudyPreferences input = buildInput("VISUAL", "CAFETERIA", "MIXTO");

        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.of(existing));
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(input);

        assertEquals(42L, result.getId());
        assertEquals("VISUAL", result.getStudyModality());
        assertEquals("CAFETERIA", result.getStudyEnvironment());
        assertEquals("MIXTO", result.getStudyMethod());
    }

    @Test
    @DisplayName("Should call repository save exactly once")
    void shouldCallRepositorySaveExactlyOnce() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput("VISUAL", "BIBLIOTECA", "INDIVIDUAL"));

        verify(preferencesRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should call findByStudentId to check for existing record")
    void shouldCheckForExistingRecord() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.save(buildInput("VISUAL", "BIBLIOTECA", "INDIVIDUAL"));

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
        assertNull(result.getStudyModality());
        assertNull(result.getStudyEnvironment());
        assertNull(result.getStudyMethod());
    }

    @Test
    @DisplayName("Should save with only some fields provided")
    void shouldSaveWithPartialFields() {
        when(preferencesRepository.findByStudentId(STUDENT_ID)).thenReturn(Optional.empty());
        when(preferencesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudyPreferences result = useCase.save(buildInput("VISUAL", null, null));

        assertEquals("VISUAL", result.getStudyModality());
        assertNull(result.getStudyEnvironment());
        assertNull(result.getStudyMethod());
    }
}
