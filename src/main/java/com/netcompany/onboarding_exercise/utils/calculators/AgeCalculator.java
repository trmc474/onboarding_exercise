package com.netcompany.onboarding_exercise.utils.calculators;

import java.time.LocalDate;
import java.time.Period;

public class AgeCalculator {
    public static Integer calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
