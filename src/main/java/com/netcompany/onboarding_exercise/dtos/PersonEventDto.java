package com.netcompany.onboarding_exercise.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonEventDto {
    private Action action;
    private Long personId;
    private PersonRequestDto personData;

    public enum Action {
        CREATE, UPDATE, DELETE
    }
}
