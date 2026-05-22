package com.aibert.dosw.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for saving or updating the student's study preferences. All fields are optional.")
public class StudyPreferencesRequestDTO {

    @Schema(example = "Virtual", description = "Preferred study modality (e.g. Virtual, Presencial, Híbrido)")
    private String studyModality;

    @Schema(example = "Biblioteca", description = "Preferred study environment (e.g. Casa, Biblioteca, Café)")
    private String studyEnvironment;

    @Size(max = 100, message = "El método de estudio no puede superar 100 caracteres")
    @Schema(example = "Mapas conceptuales", description = "Preferred study method (e.g. Mapas conceptuales, Flashcards, Resúmenes)")
    private String studyMethod;
}
