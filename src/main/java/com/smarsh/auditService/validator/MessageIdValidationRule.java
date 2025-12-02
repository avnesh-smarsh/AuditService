package com.smarsh.auditService.validator;


import com.smarsh.auditService.dto.AuditLogRequest;
import com.smarsh.auditService.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MessageIdValidationRule extends AbstractValidationRule<AuditLogRequest> {
    @Override
    protected void doValidate(AuditLogRequest request) {
        if (request.getMessageId() == null || request.getMessageId().trim().isEmpty()) {
            System.out.println(request);
            throw new ValidationException("Message ID is required");
        }
    }
}