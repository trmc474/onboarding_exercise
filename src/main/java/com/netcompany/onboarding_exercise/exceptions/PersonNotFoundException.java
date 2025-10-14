package com.netcompany.onboarding_exercise.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String message) {
        super(message);
    }
}
