package com.smarsh.auditService.exception;

import org.springframework.http.HttpStatus;

public class AuditNotFoundException extends AuditException {
    public AuditNotFoundException(String auditId) {
        super("Audit log not found: " + auditId, HttpStatus.NOT_FOUND);
    }
}