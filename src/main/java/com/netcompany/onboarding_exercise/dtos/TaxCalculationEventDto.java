package com.netcompany.onboarding_exercise.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationEventDto {
    private String taxNumber;
    private Double amount;
}
