package com.smarsh.auditService.validator;

public interface ValidationRule<T> {
    void validate(T object);
    void setNext(ValidationRule<T> next);
}
