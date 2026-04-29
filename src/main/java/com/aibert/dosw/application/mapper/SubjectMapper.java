package com.aibert.dosw.application.mapper;

import com.aibert.dosw.application.dto.request.EvaluationCutDTO;
import com.aibert.dosw.application.dto.request.SubjectRequestDTO;
import com.aibert.dosw.application.dto.response.EvaluationCutResponseDTO;
import com.aibert.dosw.application.dto.response.SubjectResponseDTO;
import com.aibert.dosw.domain.model.EvaluationCut;
import com.aibert.dosw.domain.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    Subject toDomain(SubjectRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "grade", ignore = true)
    EvaluationCut toDomain(EvaluationCutDTO dto);

    SubjectResponseDTO toResponseDTO(Subject subject);

    EvaluationCutResponseDTO toResponseDTO(EvaluationCut cut);

    List<SubjectResponseDTO> toResponseDTOList(List<Subject> subjects);

    List<EvaluationCut> toDomainCutList(List<EvaluationCutDTO> dtos);

    List<EvaluationCutResponseDTO> toResponseCutDTOList(List<EvaluationCut> cuts);
}
