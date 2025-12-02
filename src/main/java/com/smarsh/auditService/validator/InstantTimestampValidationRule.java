package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Component
public class InstantTimestampValidationRule extends AbstractValidationRule<AuditLogRequest> {

    @Override
    protected void doValidate(AuditLogRequest request) {

        Instant timestamp = request.getTimestamp();

        if (timestamp == null) {
            throw new ValidationException("Timestamp cannot be null");
        }

        try {
            // Ensure it’s a valid ISO-8601 instant string
            Instant.parse(timestamp.toString());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid timestamp format. Expected ISO-8601 (e.g., 2025-09-06T15:30:00Z)");
        }
    }
}
