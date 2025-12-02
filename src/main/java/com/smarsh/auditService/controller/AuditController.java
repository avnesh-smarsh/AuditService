package com.smarsh.auditService.controller;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.AuditNotFoundException;
import com.smarsh.auditService.model.AuditLog;
import com.smarsh.auditService.service.AuditService;
import com.smarsh.auditService.validator.AuditValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Optional;

// AuditController.java
@RestController
@RequestMapping("/api/audit")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AuditController {

    private final AuditService auditService;
    private final AuditValidator auditValidator;

    @PostMapping("/{tenantId}")
    public ResponseEntity<AuditLog> createAuditLog(
            @PathVariable String tenantId,
            @RequestBody AuditLogRequest auditLogRequest) {

        auditValidator.validateAuditRequest(auditLogRequest);
        AuditLog auditLog = convertToEntity(auditLogRequest);
        AuditLog savedLog = auditService.createAuditLog(tenantId, auditLog);

        return ResponseEntity.ok(savedLog);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<AuditLog>> getAuditsByTenant(
            @PathVariable String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        if (page == 0) {
            List<AuditLog> logs = auditService.getAuditsByTenant(tenantId);
            return ResponseEntity.ok(logs);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
            Page<AuditLog> logs = auditService.getAuditsByTenant(tenantId, pageable);
            return ResponseEntity.ok(logs.getContent());
        }
    }

    @GetMapping("/{tenantId}/message/{messageId}")
    public ResponseEntity<List<AuditLog>> getAuditsByMessageId(
            @PathVariable String tenantId,
            @PathVariable String messageId) {

        List<AuditLog> logs = auditService.getAuditsByMessageId(tenantId, messageId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{tenantId}/event-type/{eventType}")
    public ResponseEntity<List<AuditLog>> getAuditsByEventType(
            @PathVariable String tenantId,
            @PathVariable String eventType) {

        List<AuditLog> logs = auditService.getAuditsByEventType(tenantId, eventType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{tenantId}/{auditId}")
    public ResponseEntity<AuditLog> getAuditById(
            @PathVariable String tenantId,
            @PathVariable String auditId) {

        Optional<AuditLog> auditLog = auditService.getAuditById(tenantId, auditId);
        return auditLog.map(ResponseEntity::ok)
                .orElseThrow(() -> new AuditNotFoundException(auditId));
    }

    AuditLog convertToEntity(AuditLogRequest request) {
        return AuditLog.builder()
                .messageId(request.getMessageId())
                .network(request.getNetwork())
                .eventType(request.getEventType())
                .service(request.getService())
                .details(request.getDetails())
                .timestamp(request.getTimestamp())
                .build();
    }
}