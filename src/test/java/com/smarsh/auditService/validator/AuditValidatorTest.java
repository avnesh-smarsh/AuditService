package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditValidatorTest {

    @Mock
    private ValidationChainBuilder chainBuilder;

    @Mock
    private ValidationRule<AuditLogRequest> validationRule;

    @InjectMocks
    private AuditValidator auditValidator;

    private AuditLogRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = AuditLogRequest.builder()
                .messageId("msg-789")
                .network("email")
                .eventType("PROCESSED")
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();
    }

    @Test
    void validateAuditRequest_Success() {
        // Arrange
        when(chainBuilder.buildValidationChain()).thenReturn(validationRule);
        doNothing().when(validationRule).validate(validRequest);

        // Act & Assert
        assertDoesNotThrow(() -> auditValidator.validateAuditRequest(validRequest));
        verify(validationRule).validate(validRequest);
    }

    @Test
    void validateAuditRequest_ValidationException() {
        // Arrange
        when(chainBuilder.buildValidationChain()).thenReturn(validationRule);
        doThrow(new ValidationException("Validation failed")).when(validationRule).validate(validRequest);

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            auditValidator.validateAuditRequest(validRequest);
        });
        verify(validationRule).validate(validRequest);
    }
}