package com.netcompany.onboarding_exercise.utils.mappers;

import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.dtos.PersonResponseDto;
import com.netcompany.onboarding_exercise.models.Person;
import com.netcompany.onboarding_exercise.utils.calculators.AgeCalculator;

public class PersonMapper {
    public static Person convertToEntity(PersonRequestDto personRequestDto) {
        Person person = new Person();

        person.setFirstName(personRequestDto.getFirstName());
        person.setLastName(personRequestDto.getLastName());
        person.setDateOfBirth(personRequestDto.getDateOfBirth());
        person.setTaxNumber(personRequestDto.getTaxNumber());
        person.setTaxDebt(0.0);

        return person;
    }

    public static PersonResponseDto convertToResponseDto(Person person) {
        PersonResponseDto personResponseDto = new PersonResponseDto();

        personResponseDto.setId(person.getId());
        personResponseDto.setFirstName(person.getFirstName());
        personResponseDto.setLastName(person.getLastName());
        personResponseDto.setAge(AgeCalculator.calculateAge(person.getDateOfBirth()));
        personResponseDto.setTaxNumber(person.getTaxNumber());
        personResponseDto.setTaxDebt(person.getTaxDebt());

        return personResponseDto;
    }
}
