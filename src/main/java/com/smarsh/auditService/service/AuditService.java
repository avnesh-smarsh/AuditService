package com.smarsh.auditService.service;

import com.smarsh.auditService.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AuditService {
    AuditLog createAuditLog(String tenantId, AuditLog auditLog);
    List<AuditLog> getAuditsByTenant(String tenantId);
    List<AuditLog> getAuditsByMessageId(String tenantId, String messageId);
    List<AuditLog> getAuditsByEventType(String tenantId, String eventType);
    Page<AuditLog> getAuditsByTenant(String tenantId, Pageable pageable);
    Optional<AuditLog> getAuditById(String tenantId, String auditId);
}