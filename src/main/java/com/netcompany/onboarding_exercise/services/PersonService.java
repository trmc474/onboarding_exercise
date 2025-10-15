package com.netcompany.onboarding_exercise.services;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.dtos.PersonResponseDto;
import com.netcompany.onboarding_exercise.exceptions.MissingTaxNumberException;
import com.netcompany.onboarding_exercise.exceptions.PersonNotFoundException;
import com.netcompany.onboarding_exercise.exceptions.TaxNumberAlreadyExistsException;
import com.netcompany.onboarding_exercise.kafka.PersonEventProducer;
import com.netcompany.onboarding_exercise.models.Person;
import com.netcompany.onboarding_exercise.repositories.PersonRepository;
import com.netcompany.onboarding_exercise.repositories.PersonSpecification;
import com.netcompany.onboarding_exercise.utils.mappers.PersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonEventProducer personEventProducer;

    @Transactional
    public void createPerson(PersonRequestDto personRequestDto) {
        log.debug("Queueing create person with tax number '{}' to Kafka...", personRequestDto.getTaxNumber());

        // Validate before sending event
        if (personRequestDto.getTaxNumber() == null) {
            throw new MissingTaxNumberException("Tax number cannot be null or empty");
        }
        if (personRepository.existsByTaxNumber(personRequestDto.getTaxNumber())) {
            throw new TaxNumberAlreadyExistsException(
                    "Person with tax number '" + personRequestDto.getTaxNumber() + "' already exists");
        }

        // Create and send event
        PersonEventDto event =
                PersonEventDto.builder().action(PersonEventDto.Action.CREATE).personData(personRequestDto).build();

        personEventProducer.sendPersonEvent(event);
        log.info("CREATE event sent to Kafka for person with tax number '{}'", personRequestDto.getTaxNumber());
    }

    @Transactional(readOnly = true)
    public List<PersonResponseDto> getAllPersons(
            String searchField,
            String searchValue,
            Integer minAge,
            Integer maxAge,
            Double minTaxDebt,
            Double maxTaxDebt,
            Pageable pageable
    ) {
        log.debug("Fetching all persons...");

        // Build the query
        Specification<Person> specification = PersonSpecification.buildSearchAndFilterSpecification(
                searchField,
                searchValue,
                minAge,
                maxAge,
                minTaxDebt,
                maxTaxDebt
        );

        // Fetch all persons
        Page<Person> personPage = personRepository.findAll(specification, pageable);
        List<Person> persons = personPage.getContent();

        log.info("All persons fetched successfully");
        return persons.stream().map(PersonMapper::convertToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PersonResponseDto> getPersonsWithMiAndOlderThan30(Pageable pageable) {
        log.debug("Fetching persons whose name starting with 'Mi' and older than 30...");

        // Build the query
        Specification<Person> personSpecification = PersonSpecification.findPersonsWithMiAndOlderThan30();

        // Fetch persons with query and pagination
        Page<Person> personPage = personRepository.findAll(personSpecification, pageable);
        List<Person> persons = personPage.getContent();

        log.info("Persons whose name starting with 'Mi' and older than 30 fetched successfully");
        return persons.stream().map(PersonMapper::convertToResponseDto).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public PersonResponseDto getPersonById(Long id) {
        log.debug("Fetching person with ID '{}'...", id);

        // Fetch person by ID
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person with ID '" + id + "' not found"));

        log.info("Person with ID '{}' fetched successfully", person.getId());
        return PersonMapper.convertToResponseDto(person);
    }

    @Transactional(readOnly = true)
    public PersonResponseDto getPersonByTaxNumber(String taxNumber) {
        log.debug("Fetching person with tax number '{}'...", taxNumber);

        // Fetch person by tax number
        Person person = personRepository.findByTaxNumber(taxNumber)
                .orElseThrow(() -> new PersonNotFoundException("Person with tax number '" + taxNumber + "' not found"));

        log.info("Person with tax number '{}' fetched successfully.", person.getTaxNumber());
        return PersonMapper.convertToResponseDto(person);
    }

    @Transactional
    public void updatePerson(Long id, PersonRequestDto personRequestDto) {
        log.debug("Queueing update person with ID '{}' to Kafka...", id);

        // Validate before sending event
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person with ID '" + id + "' not found"));
        if (personRequestDto.getTaxNumber() != null &&
                !existingPerson.getTaxNumber().equals(personRequestDto.getTaxNumber())) {
            throw new IllegalArgumentException("Tax number cannot be updated");
        }

        // Create and send event
        PersonEventDto event = PersonEventDto.builder()
                .action(PersonEventDto.Action.UPDATE)
                .personId(id)
                .personData(personRequestDto)
                .build();

        personEventProducer.sendPersonEvent(event);
        log.info("UPDATE event sent to Kafka for person with ID '{}'", id);
    }

    @Transactional
    public void deletePerson(Long id) {
        log.debug("Queueing person deletion for ID '{}' to Kafka...", id);

        // Validate before sending event
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException("Person with ID '" + id + "' not found");
        }

        // Create and send event
        PersonEventDto event = PersonEventDto.builder().action(PersonEventDto.Action.DELETE).personId(id).build();

        personEventProducer.sendPersonEvent(event);
        log.info("DELETE event sent to Kafka for person with ID '{}'", id);
    }

    @Transactional
    public void processPersonEvent(PersonEventDto event) {
        log.debug("Processing database transaction for event: {}", event.getAction());

        switch (event.getAction()) {
            case CREATE -> {
                Person person = PersonMapper.convertToEntity(event.getPersonData());
                Person savedPerson = personRepository.save(person);
                log.info("Database transaction successful: CREATED Person with ID '{}'", savedPerson.getId());
            }
            case UPDATE -> {
                Person existingPerson = personRepository.findById(event.getPersonId())
                        .orElseThrow(() -> new PersonNotFoundException(
                                "Cannot update. Person not found with ID '" + event.getPersonId() + "'"));

                existingPerson.setFirstName(event.getPersonData().getFirstName());
                existingPerson.setLastName(event.getPersonData().getLastName());
                existingPerson.setDateOfBirth(event.getPersonData().getDateOfBirth());
                personRepository.save(existingPerson);
                log.info("Database transaction successful: UPDATED Person with ID '{}'", event.getPersonId());
            }
            case DELETE -> {
                personRepository.deleteById(event.getPersonId());
                log.info("Database transaction successful: DELETED Person with ID '{}'", event.getPersonId());
            }
            default -> log.warn("Unknown event action received: {}", event.getAction());
        }
    }

    @Transactional
    public void updateTaxDebt(String taxNumber, Double amount) {
        log.debug("Updating tax debt for tax number '{}' with amount: {}", taxNumber, amount);

        // Validate input
        if (taxNumber == null || taxNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax number cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Tax amount cannot be null");
        }

        // Find person by tax number
        Person person = personRepository.findByTaxNumber(taxNumber)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Cannot update tax debt. Person not found with tax number '" + taxNumber + "'."));

        // Add the new amount to existing tax debt
        Double currentDebt = person.getTaxDebt() != null ? person.getTaxDebt() : 0.0;
        Double newDebt = currentDebt + amount;
        person.setTaxDebt(newDebt);

        // Save the updated person
        personRepository.save(person);

        log.info(
                "Tax debt updated for person with tax number '{}'. Previous debt: {}, Added amount: {}, New debt: {}",
                taxNumber,
                currentDebt,
                amount,
                newDebt
        );
    }
}
