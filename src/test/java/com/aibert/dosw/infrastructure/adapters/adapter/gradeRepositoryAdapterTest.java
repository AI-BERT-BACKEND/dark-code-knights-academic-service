package com.aibert.dosw.infrastructure.adapters.adapter;

import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.mapper.GradePersistenceMapper;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.EvaluationCutJpaRepository;
import com.aibert.dosw.infrastructure.adapters.persistence.repository.GradeJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GradeRepositoryAdapter Tests")
class GradeRepositoryAdapterTest {

    @Mock
    private GradeJpaRepository gradeJpaRepository;

    @Mock
    private EvaluationCutJpaRepository evaluationCutJpaRepository;

    @Mock
    private GradePersistenceMapper persistenceMapper;

    @InjectMocks
    private GradeRepositoryAdapter gradeRepositoryAdapter;

    @Test
    @DisplayName("Should save grade successfully")
    void shouldSaveGradeSuccessfully() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(2L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(null)
            .build();
        
        when(evaluationCutJpaRepository.findById(2L)).thenReturn(Optional.of(cut));
        
        GradeEntity mockEntity = GradeEntity.builder()
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        when(persistenceMapper.toEntity(any(Grade.class))).thenReturn(mockEntity);
        
        Grade mockGrade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        when(persistenceMapper.toDomain(any(GradeEntity.class))).thenReturn(mockGrade);
        
        when(gradeJpaRepository.save(any(GradeEntity.class))).thenAnswer(invocation -> {
            GradeEntity savedEntity = invocation.getArgument(0);
            if (savedEntity != null) {
                savedEntity.setId(1L);
            }
            return savedEntity;
        });

        // When
        Grade result = gradeRepositoryAdapter.save(grade);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCutId()).isEqualTo(2L);
        assertThat(result.getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.getGradeValue()).isEqualTo(4.0);
        assertThat(result.getPercentage()).isEqualTo(30.0);
        verify(evaluationCutJpaRepository).findById(2L);
        verify(gradeJpaRepository).save(any(GradeEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when cut not found")
    void shouldThrowExceptionWhenCutNotFound() {
        // Given
        Grade grade = Grade.builder()
            .id(1L)
            .cutId(999L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();

        // When & Then
        assertThatThrownBy(() -> gradeRepositoryAdapter.save(grade))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Corte con id 999 no encontrado");
    }

    @Test
    @DisplayName("Should find grade by id")
    void shouldFindGradeById() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(2L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .build();
        GradeEntity entity = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();
        
        Grade mockGrade = Grade.builder()
            .id(1L)
            .cutId(2L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        
        when(gradeJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(persistenceMapper.toDomain(entity)).thenReturn(mockGrade);

        // When
        Optional<Grade> result = gradeRepositoryAdapter.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getActivityName()).isEqualTo("Midterm Exam");
        assertThat(result.get().getGradeValue()).isEqualTo(4.0);
        assertThat(result.get().getPercentage()).isEqualTo(30.0);
        verify(gradeJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional when grade not found")
    void shouldReturnEmptyOptionalWhenGradeNotFound() {
        // Given
        when(gradeJpaRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Grade> result = gradeRepositoryAdapter.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(gradeJpaRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find grades by cut id")
    void shouldFindGradesByCutId() {
        // Given
        EvaluationCutEntity cut = EvaluationCutEntity.builder()
            .id(1L)
            .cutName("Partial 1")
            .cutPercentage(30.0)
            .grade(4.0)
            .subject(null)
            .build();
        
        GradeEntity entity1 = GradeEntity.builder()
            .id(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .cut(cut)
            .build();
        GradeEntity entity2 = GradeEntity.builder()
            .id(2L)
            .activityName("Final Exam")
            .gradeValue(3.5)
            .percentage(40.0)
            .cut(cut)
            .build();
        
        Grade mockGrade1 = Grade.builder()
            .id(1L)
            .cutId(1L)
            .activityName("Midterm Exam")
            .gradeValue(4.0)
            .percentage(30.0)
            .build();
        Grade mockGrade2 = Grade.builder()
            .id(2L)
            .cutId(1L)
            .activityName("Final Exam")
            .gradeValue(3.5)
            .percentage(40.0)
            .build();
        
        when(gradeJpaRepository.findByCutId(1L)).thenReturn(List.of(entity1, entity2));
        when(persistenceMapper.toDomainList(List.of(entity1, entity2))).thenReturn(List.of(mockGrade1, mockGrade2));

        // When
        List<Grade> result = gradeRepositoryAdapter.findByCutId(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("activityName").containsExactly("Midterm Exam", "Final Exam");
        verify(gradeJpaRepository).findByCutId(1L);
    }

    @Test
    @DisplayName("Should return empty list when cut id not found")
    void shouldReturnEmptyListWhenCutIdNotFound() {
        // Given
        when(gradeJpaRepository.findByCutId(999L)).thenReturn(List.of());

        // When
        List<Grade> result = gradeRepositoryAdapter.findByCutId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(gradeJpaRepository).findByCutId(999L);
    }

    @Test
    @DisplayName("Should delete grade by id")
    void shouldDeleteGradeById() {
        // Given
        Long gradeId = 1L;

        // When
        gradeRepositoryAdapter.deleteById(gradeId);

        // Then
        verify(gradeJpaRepository).deleteById(gradeId);
    }
}
