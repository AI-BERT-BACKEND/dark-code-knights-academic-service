package com.aibert.dosw.infrastructure.adapters.persistence.mapper;

import com.aibert.dosw.domain.model.ScheduleAvailability;
import com.aibert.dosw.infrastructure.adapters.persistence.entity.ScheduleAvailabilityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleAvailabilityPersistenceMapper {

    ScheduleAvailabilityEntity toEntity(ScheduleAvailability domain);

    ScheduleAvailability toDomain(ScheduleAvailabilityEntity entity);
}
