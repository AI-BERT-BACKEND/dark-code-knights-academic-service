package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.CutCapacityExceededException;
import com.aibert.dosw.domain.exceptions.DuplicateSubjectException;
import com.aibert.dosw.domain.exceptions.GoalNotFoundException;
import com.aibert.dosw.domain.exceptions.GradeNotFoundException;
import com.aibert.dosw.domain.exceptions.ScheduleAvailabilityNotFoundException;
import com.aibert.dosw.domain.exceptions.ScheduleHoursExceedDayException;
import com.aibert.dosw.domain.exceptions.StudyPreferencesNotFoundException;
import com.aibert.dosw.domain.exceptions.EvaluationStructureLockedException;
import com.aibert.dosw.domain.exceptions.GradeOutOfRangeException;
import com.aibert.dosw.domain.exceptions.InvalidEvaluationStructureException;
import com.aibert.dosw.domain.exceptions.NoPendingCutsException;
import com.aibert.dosw.domain.exceptions.SubjectNotFoundException;
import com.aibert.dosw.domain.exceptions.SubjectNotOwnedException;
import com.aibert.dosw.entrypoints.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ScheduleAvailabilityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleScheduleAvailabilityNotFound(
            ScheduleAvailabilityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ScheduleHoursExceedDayException.class)
    public ResponseEntity<ApiResponse<Void>> handleScheduleHoursExceedDay(ScheduleHoursExceedDayException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleGoalNotFound(GoalNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(StudyPreferencesNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleStudyPreferencesNotFound(
            StudyPreferencesNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleGradeNotFound(GradeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(GradeOutOfRangeException.class)
    public ResponseEntity<ApiResponse<Void>> handleGradeOutOfRange(GradeOutOfRangeException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(CutCapacityExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleCutCapacityExceeded(CutCapacityExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleSubjectNotFound(SubjectNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(InvalidEvaluationStructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEvaluationStructure(InvalidEvaluationStructureException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(EvaluationStructureLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEvaluationStructureLocked(EvaluationStructureLockedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(DuplicateSubjectException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateSubject(DuplicateSubjectException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(SubjectNotOwnedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSubjectNotOwned(SubjectNotOwnedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(NoPendingCutsException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoPendingCuts(NoPendingCutsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Maneja body JSON malformado o tipo de contenido incorrecto.
     * Ejemplo: enviar "{ invalid json }" como body.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "El cuerpo de la petición tiene formato JSON inválido o está vacío",
                        HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Maneja path variables con tipo incorrecto.
     * Ejemplo: GET /api/v1/subjects/abc (donde 'abc' no es un Long válido).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensaje = String.format(
                "El parámetro '%s' recibió el valor '%s' que no es del tipo esperado",
                ex.getName(), ex.getValue());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(mensaje, HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Maneja headers requeridos ausentes.
     * Ejemplo: llamar a POST /api/v1/subjects sin enviar X-Student-Id.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(MissingRequestHeaderException ex) {
        String mensaje = String.format(
                "El header requerido '%s' no fue enviado en la petición",
                ex.getHeaderName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(mensaje, HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Maneja parámetros de query requeridos ausentes.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        String mensaje = String.format(
                "El parámetro requerido '%s' no fue enviado en la petición",
                ex.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(mensaje, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
