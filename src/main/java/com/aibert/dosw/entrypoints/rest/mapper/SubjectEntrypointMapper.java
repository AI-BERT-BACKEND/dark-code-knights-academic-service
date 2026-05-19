package com.aibert.dosw.entrypoints.rest.mapper;

import com.aibert.dosw.application.dto.request.SubjectRequestDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface SubjectEntrypointMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "overallAverage", ignore = true)
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "subjectName", source = "dto.subjectName")
    @Mapping(target = "credits", source = "dto.credits")
    @Mapping(target = "teacherName", source = "dto.teacherName")
    @Mapping(target = "semester", source = "dto.semester")
    @Mapping(target = "schedule", source = "dto.schedule")
    @Mapping(target = "evaluationCuts", source = "dto.evaluationCuts")
    Subject toDomain(SubjectRequestDTO dto, String studentId);
}
