package com.netcompany.onboarding_exercise.exceptions;

import com.netcompany.onboarding_exercise.templates.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handlePersonNotFoundException(PersonNotFoundException exception) {
        log.error("Person not found: {}", exception.getMessage());
        CustomErrorResponse errorResponse =
                new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaxNumberAlreadyExistsException.class)
    public ResponseEntity<CustomErrorResponse> handleTaxNumberAlreadyExistsException(
            TaxNumberAlreadyExistsException exception
    ) {
        log.error("Tax number already exist: {}", exception.getMessage());
        CustomErrorResponse errorResponse =
                new CustomErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal argument: {}", exception.getMessage());
        CustomErrorResponse errorResponse =
                new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach((error) -> errors.put(error.getField(), error.getDefaultMessage()));
        log.error("Validation error: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
