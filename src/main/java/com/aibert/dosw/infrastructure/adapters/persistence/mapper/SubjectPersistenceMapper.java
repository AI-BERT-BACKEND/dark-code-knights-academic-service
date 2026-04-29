package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.EvaluationCutEntity;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.SubjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
