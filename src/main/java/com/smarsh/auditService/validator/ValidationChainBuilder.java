package com.smarsh.auditService.validator;

import com.smarsh.auditService.dto.AuditLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationChainBuilder {

    private final NotNullValidationRule notNullRule;
    private final MessageIdValidationRule messageIdRule;
    private final NetworkValidationRule networkRule;
    private final EventTypeValidationRule eventTypeRule;
    private final ServiceValidationRule serviceRule;
    private final InstantTimestampValidationRule timestampRule;

    public ValidationRule<AuditLogRequest> buildValidationChain() {
        notNullRule.setNext(messageIdRule);
        messageIdRule.setNext(networkRule);
        networkRule.setNext(eventTypeRule);
        eventTypeRule.setNext(serviceRule);
        serviceRule.setNext(timestampRule);
        return notNullRule;
    }
}