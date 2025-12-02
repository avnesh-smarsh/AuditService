package com.smarsh.auditService.exception;

import com.smarsh.auditService.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.GET;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleAuditException_Success() {
        // Arrange
        AuditException ex = new AuditException("Test error", HttpStatus.BAD_REQUEST);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuditException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error", response.getBody().getMessage());
        assertEquals("test-path", response.getBody().getPath());
    }

    @Test
    void handleValidationExceptions_Success() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("fieldName: Error message"));
        assertEquals("test-path", response.getBody().getPath());
    }

    @Test
    void handleValidationExceptions_EmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-path", response.getBody().getPath());
    }

    @Test
    void handleGenericException_Success() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals("test-path", response.getBody().getPath());
    }

    @Test
    void handleHttpMessageNotReadableException_Success() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-path", response.getBody().getPath());
    }

    @Test
    void handleNoResourceFoundException_Success() {
        // Arrange
        org.springframework.web.servlet.resource.NoResourceFoundException ex =
                new org.springframework.web.servlet.resource.NoResourceFoundException(GET, "/invalid-path");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("test-path");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoResourceFoundException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-path", response.getBody().getPath());
    }
}