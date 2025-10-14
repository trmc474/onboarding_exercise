package com.netcompany.onboarding_exercise.exceptions;

public class TaxNumberAlreadyExistsException extends RuntimeException {
    public TaxNumberAlreadyExistsException(String message) {
        super(message);
    }
}
