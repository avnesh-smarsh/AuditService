package com.smarsh.auditService.validator;


import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EventTypeValidationRule extends AbstractValidationRule<AuditLogRequest> {
    private static final Set<String> VALID_EVENT_TYPES = Set.of(
            // From ComplianceService
            "NOTIFICATION_SENT", "NOTIFICATION_FAILED",
            "POLICY_VIOLATION", "POLICY_PASSED", "POLICY_EVALUATION_ERROR",
            "COMPLIANCE_PROCESSING_COMPLETE", "COMPLIANCE_PROCESSING_FAILED",
            "FLAG_STORED_TO_DB", "FLAG_STORE_FAILED",

            // From CanonicalService
            "ID_GENERATION_SUCCESS", "ID_GENERATION_FAILED",
            "VALIDATION_SUCCESS", "VALIDATION_FAILED",
            "DUPLICATE_DETECTED", "UNIQUE_MESSAGE", "DUPLICATE_CHECK_FAILED",
            "CANONICAL_PROCESSING_SUCCESS", "CANONICAL_PROCESSING_FAILED",
            "ES_STORAGE_SUCCESS", "ES_STORAGE_FAILED",
            "RAW_STORAGE_SUCCESS", "RAW_STORAGE_FAILED",
            "PUBLISH_TO_KAFKA_SUCCESS", "PUBLISH_TO_KAFKA_FAILED"
    );

    @Override
    protected void doValidate(AuditLogRequest request) {
        if (request.getEventType() == null || request.getEventType().trim().isEmpty()) {
            throw new ValidationException("Event type is required");
        }

        if (!VALID_EVENT_TYPES.contains(request.getEventType().toUpperCase())) {
            throw new ValidationException("Invalid event type. Must be one of: " + VALID_EVENT_TYPES);
        }
    }
}
