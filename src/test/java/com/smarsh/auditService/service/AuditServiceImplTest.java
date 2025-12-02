package com.smarsh.auditService.service;

import com.smarsh.auditService.exception.AuditException;
import com.smarsh.auditService.model.AuditLog;
import com.smarsh.auditService.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private AuditLog auditLog;
    private final String tenantId = "tenant-123";
    private final String auditId = "audit-456";

    @BeforeEach
    void setUp() {
        auditLog = AuditLog.builder()
                .auditId(auditId)
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
        when(auditRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // Act
        AuditLog result = auditService.createAuditLog(tenantId, auditLog);

        // Assert
        assertNotNull(result);
        assertEquals(tenantId, result.getTenantId());
        verify(auditRepository).save(any(AuditLog.class));
    }

    @Test
    void createAuditLog_Exception() {
        // Arrange
        when(auditRepository.save(any(AuditLog.class))).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(AuditException.class, () -> {
            auditService.createAuditLog(tenantId, auditLog);
        });
        verify(auditRepository).save(any(AuditLog.class));
    }

    @Test
    void getAuditsByTenant_Success() {
        // Arrange
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditRepository.findByTenantId(tenantId)).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditService.getAuditsByTenant(tenantId);

        // Assert
        assertEquals(1, result.size());
        verify(auditRepository).findByTenantId(tenantId);
    }

    @Test
    void getAuditsByMessageId_Success() {
        // Arrange
        String messageId = "msg-789";
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditRepository.findByTenantIdAndMessageId(tenantId, messageId)).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditService.getAuditsByMessageId(tenantId, messageId);

        // Assert
        assertEquals(1, result.size());
        verify(auditRepository).findByTenantIdAndMessageId(tenantId, messageId);
    }

    @Test
    void getAuditsByEventType_Success() {
        // Arrange
        String eventType = "PROCESSED";
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditRepository.findByTenantIdAndEventType(tenantId, eventType)).thenReturn(expectedLogs);

        // Act
        List<AuditLog> result = auditService.getAuditsByEventType(tenantId, eventType);

        // Assert
        assertEquals(1, result.size());
        verify(auditRepository).findByTenantIdAndEventType(tenantId, eventType);
    }

    @Test
    void getAuditsByTenant_WithPageable_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditLog> expectedPage = new PageImpl<>(Arrays.asList(auditLog));
        when(auditRepository.findByTenantId(tenantId, pageable)).thenReturn(expectedPage);

        // Act
        Page<AuditLog> result = auditService.getAuditsByTenant(tenantId, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        verify(auditRepository).findByTenantId(tenantId, pageable);
    }

    @Test
    void getAuditById_Success() {
        // Arrange
        when(auditRepository.findByTenantIdAndAuditId(tenantId, auditId)).thenReturn(Optional.of(auditLog));

        // Act
        Optional<AuditLog> result = auditService.getAuditById(tenantId, auditId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(auditId, result.get().getAuditId());
        verify(auditRepository).findByTenantIdAndAuditId(tenantId, auditId);
    }

    @Test
    void getAuditById_NotFound() {
        // Arrange
        when(auditRepository.findByTenantIdAndAuditId(tenantId, auditId)).thenReturn(Optional.empty());

        // Act
        Optional<AuditLog> result = auditService.getAuditById(tenantId, auditId);

        // Assert
        assertFalse(result.isPresent());
        verify(auditRepository).findByTenantIdAndAuditId(tenantId, auditId);
    }
}