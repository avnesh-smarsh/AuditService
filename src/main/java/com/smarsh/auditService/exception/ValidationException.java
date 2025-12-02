package com.smarsh.auditService.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AuditException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}