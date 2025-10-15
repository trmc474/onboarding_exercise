package com.netcompany.onboarding_exercise.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {
    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @Size(min = 2, max = 12, message = "Tax number must be between 2 and 12 digits.")
    private String taxNumber;
}
