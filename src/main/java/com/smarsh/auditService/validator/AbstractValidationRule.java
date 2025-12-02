package com.smarsh.auditService.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractValidationRule<T> implements ValidationRule<T> {
    private ValidationRule<T> next;

    @Override
    public void validate(T object) {
        doValidate(object);
        if (next != null) {
            next.validate(object);
        }
    }

    protected abstract void doValidate(T object);
}