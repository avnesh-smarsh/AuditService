package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class NetworkValidationRule extends AbstractValidationRule<AuditLogRequest> {
    @Override
    protected void doValidate(AuditLogRequest request) {
        if (request.getNetwork() == null || request.getNetwork().trim().isEmpty()) {
            throw new ValidationException("Network is required");
        }
        if (!"email".equalsIgnoreCase(request.getNetwork()) &&
                !"slack".equalsIgnoreCase(request.getNetwork())) {
            throw new ValidationException("Network must be either 'email' or 'slack'");
        }
    }
}
