package com.smarsh.auditService.dto;
// AuditLogRequest.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequest {
    private String messageId;

    private String network;

    private String eventType;

    private String service;
    private Instant timestamp;
    private Map<String, Object> details;
}

