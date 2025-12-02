package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ServiceValidationRule extends AbstractValidationRule<AuditLogRequest> {
    @Override
    protected void doValidate(AuditLogRequest request) {
        if (request.getService() == null || request.getService().trim().isEmpty()) {
            throw new ValidationException("Service name is required");
        }

        if (request.getService().length() > 100) {
            throw new ValidationException("Service name must be less than 100 characters");
        }
    }
}