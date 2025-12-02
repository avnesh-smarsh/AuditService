package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRuleTests {

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
    void messageIdValidationRule_Failure() {
        // Arrange
        MessageIdValidationRule rule = new MessageIdValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(null)  // Create new instance without messageId
                .network(validRequest.getNetwork())
                .eventType(validRequest.getEventType())
                .service(validRequest.getService())
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void networkValidationRule_Failure_Null() {
        // Arrange
        NetworkValidationRule rule = new NetworkValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network(null)  // Set network to null
                .eventType(validRequest.getEventType())
                .service(validRequest.getService())
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void networkValidationRule_Failure_Invalid() {
        // Arrange
        NetworkValidationRule rule = new NetworkValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network("invalid")  // Set invalid network
                .eventType(validRequest.getEventType())
                .service(validRequest.getService())
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void eventTypeValidationRule_Failure_Null() {
        // Arrange
        EventTypeValidationRule rule = new EventTypeValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network(validRequest.getNetwork())
                .eventType(null)  // Set eventType to null
                .service(validRequest.getService())
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void serviceValidationRule_Failure_Null() {
        // Arrange
        ServiceValidationRule rule = new ServiceValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network(validRequest.getNetwork())
                .eventType(validRequest.getEventType())
                .service(null)  // Set service to null
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void serviceValidationRule_Failure_TooLong() {
        // Arrange
        ServiceValidationRule rule = new ServiceValidationRule();
        String longServiceName = "a".repeat(101);
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network(validRequest.getNetwork())
                .eventType(validRequest.getEventType())
                .service(longServiceName)  // Set too long service name
                .timestamp(validRequest.getTimestamp())
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }

    @Test
    void instantTimestampValidationRule_Failure_Null() {
        // Arrange
        InstantTimestampValidationRule rule = new InstantTimestampValidationRule();
        AuditLogRequest invalidRequest = AuditLogRequest.builder()
                .messageId(validRequest.getMessageId())
                .network(validRequest.getNetwork())
                .eventType(validRequest.getEventType())
                .service(validRequest.getService())
                .timestamp(null)  // Set timestamp to null
                .details(validRequest.getDetails())
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> rule.doValidate(invalidRequest));
    }
}