package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.GradeRequestDTO;
import com.aibert.dosw.application.dto.request.UpdateGradeRequestDTO;
import com.aibert.dosw.application.dto.response.GradeResponseDTO;
import com.aibert.dosw.domain.model.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cutId", ignore = true)
    Grade toDomain(GradeRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cutId", ignore = true)
    Grade toDomain(UpdateGradeRequestDTO dto);

    GradeResponseDTO toResponseDTO(Grade grade);

    List<GradeResponseDTO> toResponseDTOList(List<Grade> grades);
}
