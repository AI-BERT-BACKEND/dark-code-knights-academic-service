package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubjectPersistenceMapperTest {

    private final SubjectPersistenceMapper mapper = Mappers.getMapper(SubjectPersistenceMapper.class);

    @Test
    void shouldMapSubjectEntityToDomain() {
        SubjectEntity entity = SubjectEntity.builder()
                .id(1L).studentId("s1").subjectName("Math").credits(4)
                .teacherName("T").semester("2025-1").evaluationCuts(List.of()).build();

        Subject domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getStudentId()).isEqualTo("s1");
        assertThat(domain.getSubjectName()).isEqualTo("Math");
        assertThat(domain.getCredits()).isEqualTo(4);
        assertThat(domain.getTeacherName()).isEqualTo("T");
        assertThat(domain.getSemester()).isEqualTo("2025-1");
    }

    @Test
    void shouldMapNullSubjectEntityToDomain() {
        assertThat(mapper.toDomain((SubjectEntity) null)).isNull();
    }

    @Test
    void shouldMapEvaluationCutEntityToDomain() {
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
                .id(1L).cutName("C1").cutPercentage(30.0).grade(4.0).build();

        EvaluationCut domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getCutName()).isEqualTo("C1");
        assertThat(domain.getCutPercentage()).isEqualTo(30.0);
        assertThat(domain.getGrade()).isEqualTo(4.0);
    }

    @Test
    void shouldMapNullEvaluationCutEntityToDomain() {
        assertThat(mapper.toDomain((EvaluationCutEntity) null)).isNull();
    }

    @Test
    void shouldMapSubjectDomainToEntity() {
        Subject domain = Subject.builder()
                .id(1L).studentId("s1").subjectName("Math").credits(4)
                .teacherName("T").semester("2025-1").evaluationCuts(List.of()).build();

        SubjectEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getStudentId()).isEqualTo("s1");
        assertThat(entity.getSubjectName()).isEqualTo("Math");
        assertThat(entity.getEvaluationCuts()).isNull();
    }

    @Test
    void shouldMapEvaluationCutDomainToEntity() {
        EvaluationCut domain = EvaluationCut.builder()
                .id(1L).cutName("C1").cutPercentage(30.0).grade(4.0).build();

        EvaluationCutEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getCutName()).isEqualTo("C1");
        assertThat(entity.getCutPercentage()).isEqualTo(30.0);
        assertThat(entity.getSubject()).isNull();
    }

    @Test
    void shouldMapSubjectEntityListToDomainList() {
        List<SubjectEntity> entities = List.of(
                SubjectEntity.builder().id(1L).studentId("s1").subjectName("Math").credits(4).teacherName("T").semester("2025-1").evaluationCuts(List.of()).build(),
                SubjectEntity.builder().id(2L).studentId("s2").subjectName("Physics").credits(3).teacherName("T2").semester("2025-2").evaluationCuts(List.of()).build()
        );

        List<Subject> result = mapper.toDomainList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSubjectName()).isEqualTo("Math");
        assertThat(result.get(1).getSubjectName()).isEqualTo("Physics");
    }

    @Test
    void shouldMapNullSubjectEntityListToDomainList() {
        assertThat(mapper.toDomainList(null)).isNull();
    }

    @Test
    void shouldMapEmptySubjectEntityListToDomainList() {
        assertThat(mapper.toDomainList(List.of())).isEmpty();
    }

    @Test
    void shouldMapSubjectEntityWithNullCutsToDomain() {
        SubjectEntity entity = SubjectEntity.builder()
                .id(1L).studentId("s1").subjectName("Math").credits(4)
                .teacherName("T").semester("2025-1").evaluationCuts(null).build();

        Subject domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(1L);
    }

    @Test
    void shouldMapEvaluationCutWithNullGradeToDomain() {
        EvaluationCutEntity entity = EvaluationCutEntity.builder()
                .id(1L).cutName("C1").cutPercentage(30.0).grade(null).build();

        EvaluationCut domain = mapper.toDomain(entity);

        assertThat(domain.getGrade()).isNull();
    }
}
