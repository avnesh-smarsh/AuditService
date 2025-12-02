package com.smarsh.auditService.controller;


import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.AuditNotFoundException;
import com.smarsh.auditService.model.AuditLog;
import com.smarsh.auditService.service.AuditService;
import com.smarsh.auditService.validator.AuditValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @Mock
    private AuditValidator auditValidator;

    @InjectMocks
    private AuditController auditController;

    private AuditLogRequest auditLogRequest;
    private AuditLog auditLog;
    private final String tenantId = "tenant-123";
    private final String auditId = "audit-456";

    @BeforeEach
    void setUp() {
        auditLogRequest = AuditLogRequest.builder()
                .messageId("msg-789")
                .network("email")
                .eventType("PROCESSED")
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();

        auditLog = AuditLog.builder()
                .auditId(auditId)
                .tenantId(tenantId)
                .messageId("msg-789")
                .network("email")
                .eventType("PROCESSED")
                .service("test-service")
                .timestamp(Instant.now())
                .details(Map.of("key", "value"))
                .build();
    }

    @Test
    void createAuditLog_Success() {
        // Arrange
        when(auditService.createAuditLog(eq(tenantId), any(AuditLog.class))).thenReturn(auditLog);

        // Act
        ResponseEntity<AuditLog> response = auditController.createAuditLog(tenantId, auditLogRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(auditId, response.getBody().getAuditId());
        verify(auditValidator).validateAuditRequest(auditLogRequest);
        verify(auditService).createAuditLog(eq(tenantId), any(AuditLog.class));
    }

    @Test
    void getAuditsByTenant_WithoutPagination_Success() {
        // Arrange
        List<AuditLog> logs = Arrays.asList(auditLog);
        when(auditService.getAuditsByTenant(tenantId)).thenReturn(logs);

        // Act
        ResponseEntity<List<AuditLog>> response = auditController.getAuditsByTenant(tenantId, 0, 50);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(auditService).getAuditsByTenant(tenantId);
        verify(auditService, never()).getAuditsByTenant(eq(tenantId), any(Pageable.class));
    }

    @Test
    void getAuditsByTenant_WithPagination_Success() {
        // Arrange
        Page<AuditLog> page = new PageImpl<>(Collections.singletonList(auditLog));
        when(auditService.getAuditsByTenant(eq(tenantId), any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<List<AuditLog>> response = auditController.getAuditsByTenant(tenantId, 1, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(auditService).getAuditsByTenant(eq(tenantId), any(Pageable.class));
        verify(auditService, never()).getAuditsByTenant(tenantId);
    }

    @Test
    void getAuditsByMessageId_Success() {
        // Arrange
        String messageId = "msg-789";
        List<AuditLog> logs = Arrays.asList(auditLog);
        when(auditService.getAuditsByMessageId(tenantId, messageId)).thenReturn(logs);

        // Act
        ResponseEntity<List<AuditLog>> response = auditController.getAuditsByMessageId(tenantId, messageId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(auditService).getAuditsByMessageId(tenantId, messageId);
    }

    @Test
    void getAuditsByEventType_Success() {
        // Arrange
        String eventType = "PROCESSED";
        List<AuditLog> logs = Arrays.asList(auditLog);
        when(auditService.getAuditsByEventType(tenantId, eventType)).thenReturn(logs);

        // Act
        ResponseEntity<List<AuditLog>> response = auditController.getAuditsByEventType(tenantId, eventType);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(auditService).getAuditsByEventType(tenantId, eventType);
    }

    @Test
    void getAuditById_Success() {
        // Arrange
        when(auditService.getAuditById(tenantId, auditId)).thenReturn(Optional.of(auditLog));

        // Act
        ResponseEntity<AuditLog> response = auditController.getAuditById(tenantId, auditId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(auditId, response.getBody().getAuditId());
        verify(auditService).getAuditById(tenantId, auditId);
    }

    @Test
    void getAuditById_NotFound() {
        // Arrange
        when(auditService.getAuditById(tenantId, auditId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuditNotFoundException.class, () -> {
            auditController.getAuditById(tenantId, auditId);
        });
        verify(auditService).getAuditById(tenantId, auditId);
    }

    @Test
    void convertToEntity_Success() {
        // Act
        AuditLog result = auditController.convertToEntity(auditLogRequest);

        // Assert
        assertEquals(auditLogRequest.getMessageId(), result.getMessageId());
        assertEquals(auditLogRequest.getNetwork(), result.getNetwork());
        assertEquals(auditLogRequest.getEventType(), result.getEventType());
        assertEquals(auditLogRequest.getService(), result.getService());
        assertEquals(auditLogRequest.getTimestamp(), result.getTimestamp());
        assertEquals(auditLogRequest.getDetails(), result.getDetails());
    }
}
