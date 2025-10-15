package com.netcompany.onboarding_exercise.exceptions;

public class MissingTaxNumberException extends RuntimeException {
    public MissingTaxNumberException(String message) {
        super(message);
    }
}
