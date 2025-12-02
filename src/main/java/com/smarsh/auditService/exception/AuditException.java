package com.smarsh.auditService.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class AuditException extends RuntimeException {
    private final HttpStatus status;

    public AuditException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}