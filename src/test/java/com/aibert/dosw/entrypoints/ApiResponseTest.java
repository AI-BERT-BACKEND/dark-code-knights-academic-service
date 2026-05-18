package com.aibert.dosw.entrypoints;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Test
    @DisplayName("Should create ok response with data only")
    void shouldCreateOkResponseWithDataOnly() {
        ApiResponse<String> response = ApiResponse.ok("payload");

        assertTrue(response.isSuccess());
        assertEquals("payload", response.getData());
        assertEquals("ok", response.getMessage());
        assertNull(response.getError());
        assertNull(response.getCode());
    }

    @Test
    @DisplayName("Should create ok response with data and custom message")
    void shouldCreateOkResponseWithDataAndCustomMessage() {
        ApiResponse<Integer> response = ApiResponse.ok(42, "Operación exitosa");

        assertTrue(response.isSuccess());
        assertEquals(42, response.getData());
        assertEquals("Operación exitosa", response.getMessage());
        assertNull(response.getError());
        assertNull(response.getCode());
    }

    @Test
    @DisplayName("Should create error response with message and status code")
    void shouldCreateErrorResponseWithMessageAndStatusCode() {
        ApiResponse<Void> response = ApiResponse.error("Recurso no encontrado", 404);

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNull(response.getMessage());
        assertEquals("Recurso no encontrado", response.getError());
        assertEquals(404, response.getCode());
    }

    @Test
    @DisplayName("Should expose all getters correctly")
    void shouldExposeAllGettersCorrectly() {
        ApiResponse<String> ok = ApiResponse.<String>builder()
                .success(true)
                .data("test")
                .message("msg")
                .error(null)
                .code(null)
                .build();

        assertTrue(ok.isSuccess());
        assertEquals("test", ok.getData());
        assertEquals("msg", ok.getMessage());
        assertNull(ok.getError());
        assertNull(ok.getCode());
    }

    @Test
    @DisplayName("Should accept null data in ok response")
    void shouldAcceptNullDataInOkResponse() {
        ApiResponse<String> response = ApiResponse.ok(null);

        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertEquals("ok", response.getMessage());
    }

    @Test
    @DisplayName("Should accept null data with custom message")
    void shouldAcceptNullDataWithCustomMessage() {
        ApiResponse<Void> response = ApiResponse.ok(null, "Eliminado");

        assertTrue(response.isSuccess());
        assertNull(response.getData());
        assertEquals("Eliminado", response.getMessage());
    }

    @Test
    @DisplayName("Should create error response with different status codes")
    void shouldCreateErrorResponseWithDifferentStatusCodes() {
        ApiResponse<Void> r400 = ApiResponse.error("Solicitud inválida", 400);
        ApiResponse<Void> r409 = ApiResponse.error("Conflicto", 409);
        ApiResponse<Void> r422 = ApiResponse.error("No procesable", 422);

        assertEquals(400, r400.getCode());
        assertEquals(409, r409.getCode());
        assertEquals(422, r422.getCode());
        assertFalse(r400.isSuccess());
        assertFalse(r409.isSuccess());
        assertFalse(r422.isSuccess());
    }
}
