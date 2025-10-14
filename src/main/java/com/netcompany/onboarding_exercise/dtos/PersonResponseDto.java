package com.netcompany.onboarding_exercise.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String taxNumber;
    private Double taxDebt;
}
