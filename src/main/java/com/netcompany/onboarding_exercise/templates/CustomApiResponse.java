package com.netcompany.onboarding_exercise.templates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;
    private Map<String, Object> metadata;
}
