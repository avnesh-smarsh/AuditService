package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class NotNullValidationRule extends AbstractValidationRule<AuditLogRequest> {
    @Override
    protected void doValidate(AuditLogRequest request) {
        if (request == null) {
            throw new ValidationException("Audit request cannot be null");
        }
    }
}