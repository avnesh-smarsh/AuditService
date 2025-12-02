// AuditServiceImpl.java
package com.smarsh.auditService.service;

import com.smarsh.auditService.exception.AuditException;
import com.smarsh.auditService.model.AuditLog;
import com.smarsh.auditService.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
// AuditServiceImpl.java
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    public AuditLog createAuditLog(String tenantId, AuditLog auditLog) {

        try {
            auditLog.setTenantId(tenantId);
            return auditRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to create audit log for tenant: {}", tenantId, e);
            throw new AuditException("Failed to create audit log", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<AuditLog> getAuditsByTenant(String tenantId) {

        try {
            return auditRepository.findByTenantId(tenantId);
        } catch (Exception e) {
            log.error("Failed to retrieve audit logs for tenant: {}", tenantId, e);
            throw new AuditException("Failed to retrieve audit logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<AuditLog> getAuditsByMessageId(String tenantId, String messageId) {

        try {
            return auditRepository.findByTenantIdAndMessageId(tenantId, messageId);
        } catch (Exception e) {
            log.error("Failed to retrieve audit logs for tenant: {}, message: {}", tenantId, messageId, e);
            throw new AuditException("Failed to retrieve audit logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<AuditLog> getAuditsByEventType(String tenantId, String eventType) {

        try {
            return auditRepository.findByTenantIdAndEventType(tenantId, eventType);
        } catch (Exception e) {
            log.error("Failed to retrieve audit logs for tenant: {}, event type: {}", tenantId, eventType, e);
            throw new AuditException("Failed to retrieve audit logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<AuditLog> getAuditsByTenant(String tenantId, Pageable pageable) {

        try {
            return auditRepository.findByTenantId(tenantId, pageable);
        } catch (Exception e) {
            log.error("Failed to retrieve audit logs for tenant: {}", tenantId, e);
            throw new AuditException("Failed to retrieve audit logs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Optional<AuditLog> getAuditById(String tenantId, String auditId) {

        try {
            return auditRepository.findByTenantIdAndAuditId(tenantId, auditId);
        } catch (Exception e) {
            log.error("Failed to retrieve audit log for tenant: {}, id: {}", tenantId, auditId, e);
            throw new AuditException("Failed to retrieve audit log", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}