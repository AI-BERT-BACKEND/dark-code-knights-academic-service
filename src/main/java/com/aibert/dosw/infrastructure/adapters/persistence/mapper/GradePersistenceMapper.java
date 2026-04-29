package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.Grade;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.GradeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradePersistenceMapper {

    @Mapping(target = "cutId", source = "cut.id")
    Grade toDomain(GradeEntity entity);

    @Mapping(target = "cut", ignore = true)
    GradeEntity toEntity(Grade domain);

    List<Grade> toDomainList(List<GradeEntity> entities);
}
