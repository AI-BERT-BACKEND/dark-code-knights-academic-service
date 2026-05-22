package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.StudyPreferences;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.StudyPreferencesEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudyPreferencesPersistenceMapper {

    StudyPreferences toDomain(StudyPreferencesEntity entity);

    StudyPreferencesEntity toEntity(StudyPreferences domain);
}
