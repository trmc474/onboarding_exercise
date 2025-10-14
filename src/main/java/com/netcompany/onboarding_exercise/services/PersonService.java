package com.netcompany.onboarding_exercise.services;

import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.dtos.PersonResponseDto;
import com.netcompany.onboarding_exercise.exceptions.PersonNotFoundException;
import com.netcompany.onboarding_exercise.exceptions.TaxNumberAlreadyExistsException;
import com.netcompany.onboarding_exercise.models.Person;
import com.netcompany.onboarding_exercise.repositories.PersonRepository;
import com.netcompany.onboarding_exercise.utils.mappers.PersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;

    @Transactional
    public PersonResponseDto createPerson(PersonRequestDto personRequestDto) {
        log.debug("Creating person with tax number '{}'...", personRequestDto.getTaxNumber());

        // Check if tax already exists
        if (personRepository.existsByTaxNumber(personRequestDto.getTaxNumber())) {
            throw new TaxNumberAlreadyExistsException(
                    "Person with tax number '" + personRequestDto.getTaxNumber() + "' already exists.");
        }

        Person person = PersonMapper.convertToEntity(personRequestDto);
        Person savedPerson = personRepository.save(person);

        log.info("Person created successfully with ID '{}'.", savedPerson.getId());
        return PersonMapper.convertToResponseDto(savedPerson);
    }

    @Transactional(readOnly = true)
    public List<PersonResponseDto> getAllPersons() {
        log.debug("Fetching all persons...");

        List<Person> persons = personRepository.findAll();

        log.info("All persons fetched successfully.");
        return persons.stream().map(PersonMapper::convertToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonResponseDto getPersonById(Long id) {
        log.debug("Fetching person with ID '{}'...", id);

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID '" + id + "'."));

        log.info("Person fetch successfully with ID '{}'.", person.getId());
        return PersonMapper.convertToResponseDto(person);
    }

    @Transactional(readOnly = true)
    public PersonResponseDto getPersonByTaxNumber(String taxNumber) {
        log.debug("Fetching person with tax number '{}'...", taxNumber);

        Person person = personRepository.findByTaxNumber(taxNumber)
                .orElseThrow(
                        () -> new PersonNotFoundException("Person not found with tax number '" + taxNumber + "'."));

        log.info("Person fetch successfully with tax number '{}'.", person.getTaxNumber());
        return PersonMapper.convertToResponseDto(person);
    }

    @Transactional
    public PersonResponseDto updatePerson(Long id, PersonRequestDto personRequestDto) {
        log.debug("Updating person with ID '{}'.", id);

        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID '" + id + "'."));

        // Ensure tax number is not being changed
        if (!existingPerson.getTaxNumber().equals(personRequestDto.getTaxNumber())) {
            throw new IllegalArgumentException("Tax number cannot be updated.");
        }

        existingPerson.setFirstName(personRequestDto.getFirstName());
        existingPerson.setLastName(personRequestDto.getLastName());
        existingPerson.setDateOfBirth(personRequestDto.getDateOfBirth());

        Person updatedPerson = personRepository.save(existingPerson);

        log.info("Person updated successfully with ID '{}'.", updatedPerson.getId());
        return PersonMapper.convertToResponseDto(updatedPerson);
    }

    @Transactional
    public void deletePerson(Long id) {
        log.debug("Deleting person with ID '{}'.", id);

        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException("Person not found with ID '" + id + "'.");
        }

        personRepository.deleteById(id);

        log.info("Person deleted successfully with ID '{}'.", id);
    }
}
