package com.smarsh.auditService.validator;

import com.smarsh.auditService.exception.ValidationException;
import com.smarsh.auditService.dto.AuditLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditValidator {

    private final ValidationChainBuilder chainBuilder;

    public void validateAuditRequest(AuditLogRequest request) {
        ValidationRule<AuditLogRequest> validationChain = chainBuilder.buildValidationChain();
        validationChain.validate(request);
    }
}