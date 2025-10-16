package com.netcompany.onboarding_exercise.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonEventDto {
    private Action action;
    private Long personId;
    private PersonRequestDto personData;

    private ProcessingMode processingMode;

    public enum Action {
        CREATE, UPDATE, DELETE
    }

    public enum ProcessingMode {
        DEPENDENT, INDEPENDENT
    }
}
