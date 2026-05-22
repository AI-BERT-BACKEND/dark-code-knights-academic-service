package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectPersistenceMapper {

    Subject toDomain(SubjectEntity entity);

    EvaluationCut toDomain(EvaluationCutEntity entity);

    @Mapping(target = "evaluationCuts", ignore = true)
    SubjectEntity toEntity(Subject domain);

    @Mapping(target = "subject", ignore = true)
    EvaluationCutEntity toEntity(EvaluationCut domain);

    List<Subject> toDomainList(List<SubjectEntity> entities);

    @AfterMapping
    default void computeOverallAverage(SubjectEntity entity, @MappingTarget Subject.SubjectBuilder builder) {
        if (entity.getEvaluationCuts() == null || entity.getEvaluationCuts().isEmpty()) {
            return;
        }
        boolean anyGraded = entity.getEvaluationCuts().stream()
                .anyMatch(cut -> cut.getGrade() != null);
        if (!anyGraded) {
            return;
        }
        double avg = entity.getEvaluationCuts().stream()
                .filter(cut -> cut.getGrade() != null)
                .mapToDouble(cut -> cut.getGrade() * cut.getCutPercentage())
                .sum() / 100.0;
        builder.overallAverage(avg);
    }
}
