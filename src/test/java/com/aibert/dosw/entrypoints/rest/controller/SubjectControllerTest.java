package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.SubjectRequestDTO;
import com.aibert.dosw.application.dto.response.SubjectResponseDTO;
import com.aibert.dosw.application.mapper.SubjectMapper;
import com.aibert.dosw.domain.model.Subject;
import com.aibert.dosw.domain.ports.in.CreateSubjectUseCase;
import com.aibert.dosw.domain.ports.in.DeleteSubjectUseCase;
import com.aibert.dosw.domain.ports.in.GetSubjectsUseCase;
import com.aibert.dosw.domain.ports.in.UpdateSubjectUseCase;
import com.aibert.dosw.entrypoints.ApiResponse;
import com.aibert.dosw.entrypoints.rest.mapper.SubjectEntrypointMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectController Tests")
class SubjectControllerTest {

    @Mock
    private CreateSubjectUseCase createSubjectUseCase;

    @Mock
    private GetSubjectsUseCase getSubjectsUseCase;

    @Mock
    private UpdateSubjectUseCase updateSubjectUseCase;

    @Mock
    private DeleteSubjectUseCase deleteSubjectUseCase;

    @Mock
    private SubjectEntrypointMapper entrypointMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectController subjectController;

    private String testStudentId;
    private SubjectRequestDTO testRequest;
    private Subject testSubject;
    private SubjectResponseDTO testResponse;

    @BeforeEach
    void setUp() {
        testStudentId = "student123";
        testRequest = SubjectRequestDTO.builder()
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .build();
        
        testSubject = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of())
            .build();
        
        testResponse = SubjectResponseDTO.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Mathematics")
            .semester("2025-1")
            .credits(4)
            .teacherName("Dr. Smith")
            .evaluationCuts(List.of())
            .build();
    }

    @Test
    @DisplayName("Should create subject successfully")
    void shouldCreateSubjectSuccessfully() {
        // Given
        when(entrypointMapper.toDomain(testRequest, testStudentId)).thenReturn(testSubject);
        when(createSubjectUseCase.create(testSubject)).thenReturn(testSubject);
        when(subjectMapper.toResponseDTO(testSubject)).thenReturn(testResponse);

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response = 
            subjectController.create(testStudentId, testRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        ApiResponse<SubjectResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());
        
        SubjectResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(1L, responseData.getId());
        assertEquals("Mathematics", responseData.getSubjectName());
        assertEquals(testStudentId, responseData.getStudentId());
        
        verify(entrypointMapper, times(1)).toDomain(testRequest, testStudentId);
        verify(createSubjectUseCase, times(1)).create(testSubject);
        verify(subjectMapper, times(1)).toResponseDTO(testSubject);
    }

    @Test
    @DisplayName("Should get all subjects by student ID")
    void shouldGetAllSubjectsByStudentId() {
        // Given
        List<Subject> subjects = List.of(testSubject);
        List<SubjectResponseDTO> responseDTOs = List.of(testResponse);
        
        when(getSubjectsUseCase.getAllByStudent(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseDTOList(subjects)).thenReturn(responseDTOs);

        // When
        ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> response = 
            subjectController.getAll(testStudentId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<List<SubjectResponseDTO>> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());
        
        List<SubjectResponseDTO> responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(1, responseData.size());
        assertEquals("Mathematics", responseData.get(0).getSubjectName());
        
        verify(getSubjectsUseCase, times(1)).getAllByStudent(testStudentId);
        verify(subjectMapper, times(1)).toResponseDTOList(subjects);
    }

    @Test
    @DisplayName("Should get empty list when student has no subjects")
    void shouldGetEmptyListWhenStudentHasNoSubjects() {
        // Given
        List<Subject> emptySubjects = List.of();
        List<SubjectResponseDTO> emptyResponseDTOs = List.of();
        
        when(getSubjectsUseCase.getAllByStudent(testStudentId)).thenReturn(emptySubjects);
        when(subjectMapper.toResponseDTOList(emptySubjects)).thenReturn(emptyResponseDTOs);

        // When
        ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> response = 
            subjectController.getAll(testStudentId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<List<SubjectResponseDTO>> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        
        List<SubjectResponseDTO> responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertTrue(responseData.isEmpty());
        
        verify(getSubjectsUseCase, times(1)).getAllByStudent(testStudentId);
        verify(subjectMapper, times(1)).toResponseDTOList(emptySubjects);
    }

    @Test
    @DisplayName("Should get subject by ID and student ID")
    void shouldGetSubjectById() {
        // Given
        Long subjectId = 1L;
        when(getSubjectsUseCase.getByIdAndStudent(subjectId, testStudentId)).thenReturn(testSubject);
        when(subjectMapper.toResponseDTO(testSubject)).thenReturn(testResponse);

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response =
            subjectController.getById(testStudentId, subjectId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<SubjectResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());

        SubjectResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(1L, responseData.getId());
        assertEquals("Mathematics", responseData.getSubjectName());

        verify(getSubjectsUseCase, times(1)).getByIdAndStudent(subjectId, testStudentId);
        verify(subjectMapper, times(1)).toResponseDTO(testSubject);
    }

    @Test
    @DisplayName("Should update subject successfully")
    void shouldUpdateSubjectSuccessfully() {
        // Given
        Long subjectId = 1L;
        SubjectRequestDTO updateRequest = SubjectRequestDTO.builder()
            .subjectName("Advanced Mathematics")
            .semester("2025-1")
            .credits(5)
            .teacherName("Dr. Johnson")
            .build();
        
        Subject updatedSubject = Subject.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Advanced Mathematics")
            .semester("2025-1")
            .credits(5)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
        
        SubjectResponseDTO updatedResponse = SubjectResponseDTO.builder()
            .id(1L)
            .studentId(testStudentId)
            .subjectName("Advanced Mathematics")
            .semester("2025-1")
            .credits(5)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
        
        when(entrypointMapper.toDomain(updateRequest, testStudentId)).thenReturn(updatedSubject);
        when(updateSubjectUseCase.update(subjectId, updatedSubject)).thenReturn(updatedSubject);
        when(subjectMapper.toResponseDTO(updatedSubject)).thenReturn(updatedResponse);

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response = 
            subjectController.update(subjectId, testStudentId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ApiResponse<SubjectResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("ok", apiResponse.getMessage());
        
        SubjectResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData);
        assertEquals(1L, responseData.getId());
        assertEquals("Advanced Mathematics", responseData.getSubjectName());
        assertEquals(5, responseData.getCredits());
        assertEquals("Dr. Johnson", responseData.getTeacherName());
        
        verify(entrypointMapper, times(1)).toDomain(updateRequest, testStudentId);
        verify(updateSubjectUseCase, times(1)).update(subjectId, updatedSubject);
        verify(subjectMapper, times(1)).toResponseDTO(updatedSubject);
    }

    @Test
    @DisplayName("Should delete subject successfully")
    void shouldDeleteSubjectSuccessfully() {
        // Given
        Long subjectId = 1L;
        doNothing().when(deleteSubjectUseCase).delete(subjectId, testStudentId);

        // When
        ResponseEntity<Void> response =
            subjectController.delete(testStudentId, subjectId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(response.hasBody());

        verify(deleteSubjectUseCase, times(1)).delete(subjectId, testStudentId);
    }

    @Test
    @DisplayName("Should handle different student IDs")
    void shouldHandleDifferentStudentIds() {
        // Given
        String differentStudentId = "student456";
        Subject differentSubject = Subject.builder()
            .id(2L)
            .studentId(differentStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Brown")
            .evaluationCuts(List.of())
            .build();
        
        when(entrypointMapper.toDomain(testRequest, differentStudentId)).thenReturn(differentSubject);
        when(createSubjectUseCase.create(differentSubject)).thenReturn(differentSubject);
        when(subjectMapper.toResponseDTO(differentSubject)).thenReturn(
            SubjectResponseDTO.builder()
                .id(2L)
                .studentId(differentStudentId)
                .subjectName("Physics")
                .semester("2025-1")
                .credits(3)
                .teacherName("Dr. Brown")
                .evaluationCuts(List.of())
                .build()
        );

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response = 
            subjectController.create(differentStudentId, testRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        SubjectResponseDTO responseData = response.getBody().getData();
        assertEquals(differentStudentId, responseData.getStudentId());
        assertEquals("Physics", responseData.getSubjectName());
        
        verify(entrypointMapper, times(1)).toDomain(testRequest, differentStudentId);
        verify(createSubjectUseCase, times(1)).create(differentSubject);
    }

    @Test
    @DisplayName("Should handle boundary values in subject creation")
    void shouldHandleBoundaryValuesInSubjectCreation() {
        // Given
        SubjectRequestDTO boundaryRequest = SubjectRequestDTO.builder()
            .subjectName("")
            .semester("")
            .credits(0)
            .teacherName("")
            .build();
        
        Subject boundarySubject = Subject.builder()
            .id(3L)
            .studentId(testStudentId)
            .subjectName("")
            .semester("")
            .credits(0)
            .teacherName("")
            .evaluationCuts(List.of())
            .build();
        
        when(entrypointMapper.toDomain(boundaryRequest, testStudentId)).thenReturn(boundarySubject);
        when(createSubjectUseCase.create(boundarySubject)).thenReturn(boundarySubject);
        when(subjectMapper.toResponseDTO(boundarySubject)).thenReturn(
            SubjectResponseDTO.builder()
                .id(3L)
                .studentId(testStudentId)
                .subjectName("")
                .semester("")
                .credits(0)
                .teacherName("")
                .evaluationCuts(List.of())
                .build()
        );

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response = 
            subjectController.create(testStudentId, boundaryRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        SubjectResponseDTO responseData = response.getBody().getData();
        assertEquals("", responseData.getSubjectName());
        assertEquals(0, responseData.getCredits());
        
        verify(entrypointMapper, times(1)).toDomain(boundaryRequest, testStudentId);
        verify(createSubjectUseCase, times(1)).create(boundarySubject);
    }

    @Test
    @DisplayName("Should verify response structure for create operation")
    void shouldVerifyResponseStructureForCreateOperation() {
        // Given
        when(entrypointMapper.toDomain(testRequest, testStudentId)).thenReturn(testSubject);
        when(createSubjectUseCase.create(testSubject)).thenReturn(testSubject);
        when(subjectMapper.toResponseDTO(testSubject)).thenReturn(testResponse);

        // When
        ResponseEntity<ApiResponse<SubjectResponseDTO>> response = 
            subjectController.create(testStudentId, testRequest);

        // Then
        assertNotNull(response);
        assertTrue(response.hasBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        ApiResponse<SubjectResponseDTO> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getData());
        assertNotNull(apiResponse.getMessage());
        
        SubjectResponseDTO responseData = apiResponse.getData();
        assertNotNull(responseData.getId());
        assertNotNull(responseData.getStudentId());
        assertNotNull(responseData.getSubjectName());
        
        verify(entrypointMapper, times(1)).toDomain(testRequest, testStudentId);
        verify(createSubjectUseCase, times(1)).create(testSubject);
        verify(subjectMapper, times(1)).toResponseDTO(testSubject);
    }

    @Test
    @DisplayName("Should verify response structure for delete operation")
    void shouldVerifyResponseStructureForDeleteOperation() {
        // Given
        Long subjectId = 1L;
        doNothing().when(deleteSubjectUseCase).delete(subjectId, testStudentId);

        // When
        ResponseEntity<Void> response =
            subjectController.delete(testStudentId, subjectId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(response.hasBody());

        verify(deleteSubjectUseCase, times(1)).delete(subjectId, testStudentId);
    }

    @Test
    @DisplayName("Should handle multiple subjects in getAll operation")
    void shouldHandleMultipleSubjectsInGetAllOperation() {
        // Given
        Subject subject2 = Subject.builder()
            .id(2L)
            .studentId(testStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
        
        SubjectResponseDTO response2 = SubjectResponseDTO.builder()
            .id(2L)
            .studentId(testStudentId)
            .subjectName("Physics")
            .semester("2025-1")
            .credits(3)
            .teacherName("Dr. Johnson")
            .evaluationCuts(List.of())
            .build();
        
        List<Subject> subjects = List.of(testSubject, subject2);
        List<SubjectResponseDTO> responseDTOs = List.of(testResponse, response2);
        
        when(getSubjectsUseCase.getAllByStudent(testStudentId)).thenReturn(subjects);
        when(subjectMapper.toResponseDTOList(subjects)).thenReturn(responseDTOs);

        // When
        ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> response = 
            subjectController.getAll(testStudentId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<SubjectResponseDTO> responseData = response.getBody().getData();
        assertNotNull(responseData);
        assertEquals(2, responseData.size());
        assertEquals("Mathematics", responseData.get(0).getSubjectName());
        assertEquals("Physics", responseData.get(1).getSubjectName());
        
        verify(getSubjectsUseCase, times(1)).getAllByStudent(testStudentId);
        verify(subjectMapper, times(1)).toResponseDTOList(subjects);
    }
}
