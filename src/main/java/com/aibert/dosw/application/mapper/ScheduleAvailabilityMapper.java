package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.ScheduleAvailabilityRequestDTO;
import com.aibert.dosw.application.dto.response.ScheduleAvailabilityResponseDTO;
import com.aibert.dosw.domain.model.ScheduleAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleAvailabilityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    ScheduleAvailability toDomain(ScheduleAvailabilityRequestDTO dto);

    @Mapping(target = "configId", source = "id")
    ScheduleAvailabilityResponseDTO toResponse(ScheduleAvailability domain);
}
