package com.netcompany.onboarding_exercise.templates;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
    private boolean success;
    private int status;
    private String message;
}
