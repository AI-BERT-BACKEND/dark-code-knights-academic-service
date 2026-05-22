package com.aibert.dosw.entrypoints.advice;

import com.aibert.dosw.domain.exceptions.*;
import com.aibert.dosw.entrypoints.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_returnsBadRequest() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "field", "field is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError()).contains("field is required");
    }

    @Test
    void handleGradeNotFound_returnsNotFound() {
        GradeNotFoundException ex = new GradeNotFoundException(1L);
        ResponseEntity<ApiResponse<Void>> response = handler.handleGradeNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError()).contains("1");
    }

    @Test
    void handleGradeOutOfRange_returnsUnprocessableEntity() {
        GradeOutOfRangeException ex = new GradeOutOfRangeException(6.0);
        ResponseEntity<ApiResponse<Void>> response = handler.handleGradeOutOfRange(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleCutCapacityExceeded_returnsUnprocessableEntity() {
        CutCapacityExceededException ex = new CutCapacityExceededException(90.0, 20.0);
        ResponseEntity<ApiResponse<Void>> response = handler.handleCutCapacityExceeded(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleSubjectNotFound_returnsNotFound() {
        SubjectNotFoundException ex = new SubjectNotFoundException(99L);
        ResponseEntity<ApiResponse<Void>> response = handler.handleSubjectNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleInvalidEvaluationStructure_returnsBadRequest() {
        InvalidEvaluationStructureException ex = new InvalidEvaluationStructureException("Invalid structure");
        ResponseEntity<ApiResponse<Void>> response = handler.handleInvalidEvaluationStructure(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleEvaluationStructureLocked_returnsConflict() {
        EvaluationStructureLockedException ex = new EvaluationStructureLockedException(1L);
        ResponseEntity<ApiResponse<Void>> response = handler.handleEvaluationStructureLocked(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleDuplicateSubject_returnsConflict() {
        DuplicateSubjectException ex = new DuplicateSubjectException("Math", "2025-1");
        ResponseEntity<ApiResponse<Void>> response = handler.handleDuplicateSubject(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleNoPendingCuts_returnsUnprocessableEntity() {
        NoPendingCutsException ex = new NoPendingCutsException(1L);
        ResponseEntity<ApiResponse<Void>> response = handler.handleNoPendingCuts(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ApiResponse<Void>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError()).isEqualTo("Invalid argument");
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ApiResponse<Void>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getError()).isEqualTo("Error interno del servidor");
    }

    @Test
    @DisplayName("Should return 400 when JSON body is malformed")
    void handleMessageNotReadable_returnsBadRequest() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

        ResponseEntity<ApiResponse<Void>> response = handler.handleMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(400);
        assertThat(response.getBody().getError())
                .isEqualTo("El cuerpo de la petición tiene formato JSON inválido o está vacío");
    }

    @Test
    @DisplayName("Should return 400 when path variable has wrong type")
    void handleTypeMismatch_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("subjectId");
        when(ex.getValue()).thenReturn("abc");

        ResponseEntity<ApiResponse<Void>> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(400);
        assertThat(response.getBody().getError())
                .contains("subjectId")
                .contains("abc");
    }

    @Test
    @DisplayName("Should return 400 when required header is missing")
    void handleMissingHeader_returnsBadRequest() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("studentId");

        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingHeader(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(400);
        assertThat(response.getBody().getError())
                .contains("studentId");
    }
}
