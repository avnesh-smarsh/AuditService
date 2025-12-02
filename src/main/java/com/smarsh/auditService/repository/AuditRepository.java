package com.smarsh.auditService.repository;

import com.smarsh.auditService.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AuditRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByTenantId(String tenantId);
    List<AuditLog> findByTenantIdAndMessageId(String tenantId, String messageId);
    List<AuditLog> findByTenantIdAndEventType(String tenantId, String eventType);
    Page<AuditLog> findByTenantId(String tenantId, Pageable pageable);

    // Additional query methods with tenant enforcement
    List<AuditLog> findByTenantIdAndTimestampBetween(String tenantId, Instant start, Instant end);
    Optional<AuditLog> findByTenantIdAndAuditId(String tenantId, String auditId);
}
