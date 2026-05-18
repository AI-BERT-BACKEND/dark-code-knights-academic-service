package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.AcademicGoal;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.AcademicGoalEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AcademicGoalPersistenceMapper {

    AcademicGoal toDomain(AcademicGoalEntity entity);

    AcademicGoalEntity toEntity(AcademicGoal domain);

    List<AcademicGoal> toDomainList(List<AcademicGoalEntity> entities);
}
