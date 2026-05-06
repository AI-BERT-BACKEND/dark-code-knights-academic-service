package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SimulationRequestDTO;
import com.aibert.dosw.application.dto.response.SimulationResponseDTO;
import com.aibert.dosw.domain.model.SimulationResult;
import com.aibert.dosw.domain.ports.in.SimulateTargetGradeUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SimulationController Tests")
class SimulationControllerTest {

    @Mock
    private SimulateTargetGradeUseCase simulateTargetGradeUseCase;

    @InjectMocks
    private SimulationController simulationController;

    private Long testSubjectId;
    private SimulationRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testSubjectId = 1L;
        testRequest = SimulationRequestDTO.builder()
            .targetGrade(4.0)
            .build();
    }

    @Test
    @DisplayName("Should simulate successfully with achievable grade")
    void shouldSimulateSuccessfullyWithAchievableGrade() {
        // Given
        SimulationResult result = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 4.0)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, testRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiResponse<SimulationResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());
        
        SimulationResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(4.0, responseData.getTargetGrade());
        assertEquals(3.5, responseData.getRequiredGrade());
        assertTrue(responseData.isAchievable());
        assertEquals(70.0, responseData.getPendingCutsPercentage());
        assertEquals("Para alcanzar 4,0 necesitas obtener 3,50 o más en los cortes pendientes (70% restante).", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 4.0);
    }

    @Test
    @DisplayName("Should simulate successfully with unachievable grade")
    void shouldSimulateSuccessfullyWithUnachievableGrade() {
        // Given
        SimulationResult result = SimulationResult.builder()
            .targetGrade(5.0)
            .requiredGrade(6.0)
            .achievable(false)
            .pendingPercentage(50.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 5.0)).thenReturn(result);

        // When
        SimulationRequestDTO unachievableRequest = SimulationRequestDTO.builder()
            .targetGrade(5.0)
            .build();
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, unachievableRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiResponse<SimulationResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        
        SimulationResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(5.0, responseData.getTargetGrade());
        assertEquals(6.0, responseData.getRequiredGrade());
        assertFalse(responseData.isAchievable());
        assertEquals(50.0, responseData.getPendingCutsPercentage());
        assertEquals("No es posible alcanzar 5,0. La nota requerida (6,00) supera el máximo permitido (5.0).", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 5.0);
    }

    @Test
    @DisplayName("Should simulate successfully with zero required grade")
    void shouldSimulateSuccessfullyWithZeroRequiredGrade() {
        // Given
        SimulationResult result = SimulationResult.builder()
            .targetGrade(3.0)
            .requiredGrade(0.0)
            .achievable(true)
            .pendingPercentage(30.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 3.0)).thenReturn(result);

        // When
        SimulationRequestDTO zeroRequest = SimulationRequestDTO.builder()
            .targetGrade(3.0)
            .build();
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, zeroRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        ApiResponse<SimulationResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        
        SimulationResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(3.0, responseData.getTargetGrade());
        assertEquals(0.0, responseData.getRequiredGrade());
        assertTrue(responseData.isAchievable());
        assertEquals(30.0, responseData.getPendingCutsPercentage());
        assertEquals("¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás 3,0.", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 3.0);
    }

    @Test
    @DisplayName("Should handle boundary values correctly")
    void shouldHandleBoundaryValuesCorrectly() {
        // Given
        SimulationRequestDTO boundaryRequest = SimulationRequestDTO.builder()
            .targetGrade(0.0)
            .build();
        
        SimulationResult result = SimulationResult.builder()
            .targetGrade(0.0)
            .requiredGrade(0.0)
            .achievable(true)
            .pendingPercentage(100.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 0.0)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, boundaryRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        SimulationResponseDTO responseData = response.getBody().getData();
        assertNotNull(responseData);
        assertEquals(0.0, responseData.getTargetGrade());
        assertEquals(0.0, responseData.getRequiredGrade());
        assertTrue(responseData.isAchievable());
        assertEquals(100.0, responseData.getPendingCutsPercentage());
        assertEquals("¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás 0,0.", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 0.0);
    }

    @Test
    @DisplayName("Should handle maximum grade values")
    void shouldHandleMaximumGradeValues() {
        // Given
        SimulationRequestDTO maxRequest = SimulationRequestDTO.builder()
            .targetGrade(5.0)
            .build();
        
        SimulationResult result = SimulationResult.builder()
            .targetGrade(5.0)
            .requiredGrade(5.0)
            .achievable(true)
            .pendingPercentage(25.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 5.0)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, maxRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        SimulationResponseDTO responseData = response.getBody().getData();
        assertNotNull(responseData);
        assertEquals(5.0, responseData.getTargetGrade());
        assertEquals(5.0, responseData.getRequiredGrade());
        assertTrue(responseData.isAchievable());
        assertEquals(25.0, responseData.getPendingCutsPercentage());
        assertEquals("Para alcanzar 5,0 necesitas obtener 5,00 o más en los cortes pendientes (25% restante).", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 5.0);
    }

    @Test
    @DisplayName("Should handle decimal grade values correctly")
    void shouldHandleDecimalGradeValuesCorrectly() {
        // Given
        SimulationRequestDTO decimalRequest = SimulationRequestDTO.builder()
            .targetGrade(3.7)
            .build();
        
        SimulationResult result = SimulationResult.builder()
            .targetGrade(3.7)
            .requiredGrade(4.25)
            .achievable(true)
            .pendingPercentage(45.5)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 3.7)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, decimalRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        SimulationResponseDTO responseData = response.getBody().getData();
        assertNotNull(responseData);
        assertEquals(3.7, responseData.getTargetGrade());
        assertEquals(4.25, responseData.getRequiredGrade());
        assertTrue(responseData.isAchievable());
        assertEquals(45.5, responseData.getPendingCutsPercentage());
        assertEquals("Para alcanzar 3,7 necesitas obtener 4,25 o más en los cortes pendientes (46% restante).", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 3.7);
    }

    @Test
    @DisplayName("Should handle different subject IDs")
    void shouldHandleDifferentSubjectIds() {
        // Given
        Long differentSubjectId = 999L;
        SimulationResult result = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(differentSubjectId, 4.0)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(differentSubjectId, testRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(differentSubjectId, 4.0);
    }

    @Test
    @DisplayName("Should verify response structure")
    void shouldVerifyResponseStructure() {
        // Given
        SimulationResult result = SimulationResult.builder()
            .targetGrade(4.0)
            .requiredGrade(3.5)
            .achievable(true)
            .pendingPercentage(70.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 4.0)).thenReturn(result);

        // When
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, testRequest);

        // Then
        assertNotNull(response);
        assertTrue(response.hasBody());
        
        ApiResponse<SimulationResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getData());
        assertNotNull(apiResponse.getMessage());
        
        SimulationResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData.getTargetGrade());
        assertNotNull(responseData.getRequiredGrade());
        assertNotNull(responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 4.0);
    }

    @Test
    @DisplayName("Should handle very small required grade")
    void shouldHandleVerySmallRequiredGrade() {
        // Given
        SimulationResult result = SimulationResult.builder()
            .targetGrade(1.0)
            .requiredGrade(0.01)
            .achievable(true)
            .pendingPercentage(20.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 1.0)).thenReturn(result);

        // When
        SimulationRequestDTO smallRequest = SimulationRequestDTO.builder()
            .targetGrade(1.0)
            .build();
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response = 
            simulationController.simulate(testSubjectId, smallRequest);

        // Then
        assertNotNull(response);
        SimulationResponseDTO responseData = response.getBody().getData();
        assertEquals("Para alcanzar 1,0 necesitas obtener 0,01 o más en los cortes pendientes (20% restante).", 
                     responseData.getMessage());
        
        verify(simulateTargetGradeUseCase, times(1)).simulate(testSubjectId, 1.0);
    }

    @Test
    @DisplayName("Should handle message formatting with different scenarios")
    void shouldHandleMessageFormattingWithDifferentScenarios() {
        // Test scenario 1: Unachievable with high required grade
        SimulationResult unachievableResult = SimulationResult.builder()
            .targetGrade(4.5)
            .requiredGrade(10.5)
            .achievable(false)
            .pendingPercentage(60.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 4.5)).thenReturn(unachievableResult);

        SimulationRequestDTO unachievableFormatRequest = SimulationRequestDTO.builder()
            .targetGrade(4.5)
            .build();
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response1 = 
            simulationController.simulate(testSubjectId, unachievableFormatRequest);
        
        assertEquals("No es posible alcanzar 4,5. La nota requerida (10,50) supera el máximo permitido (5.0).", 
                     response1.getBody().getData().getMessage());

        // Test scenario 2: Zero required grade
        SimulationResult zeroResult = SimulationResult.builder()
            .targetGrade(2.0)
            .requiredGrade(0.0)
            .achievable(true)
            .pendingPercentage(15.0)
            .build();
        
        when(simulateTargetGradeUseCase.simulate(testSubjectId, 2.0)).thenReturn(zeroResult);

        SimulationRequestDTO zeroFormatRequest = SimulationRequestDTO.builder()
            .targetGrade(2.0)
            .build();
        ResponseEntity<ApiResponse<SimulationResponseDTO>> response2 = 
            simulationController.simulate(testSubjectId, zeroFormatRequest);
        
        assertEquals("¡Ya tienes asegurado superar tu meta! Con cualquier nota en los cortes pendientes alcanzarás 2,0.", 
                     response2.getBody().getData().getMessage());

        verify(simulateTargetGradeUseCase, times(2)).simulate(anyLong(), anyDouble());
    }
}
