package com.netcompany.onboarding_exercise.controllers;

import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.dtos.PersonResponseDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person", description = "API endpoints for person management.")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    @Operation(summary = "Create Person")
    public ResponseEntity<PersonResponseDto> createPerson(@Valid @RequestBody PersonRequestDto personRequestDto) {
        log.info("POST /api/persons - Create person with tax number '{}'.", personRequestDto.getTaxNumber());

        PersonResponseDto response = personService.createPerson(personRequestDto);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    @Operation(summary = "Get All Persons")
    public ResponseEntity<List<PersonResponseDto>> getAllPersons() {
        log.info("GET /api/persons - Fetch all person.");

        List<PersonResponseDto> response = personService.getAllPersons();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Person by ID")
    public ResponseEntity<PersonResponseDto> getPersonById(@PathVariable Long id) {
        log.info("GET /api/persons/{} - Fetch person with ID '{}'.", id, id);

        PersonResponseDto response = personService.getPersonById(id);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/tax-number/{taxNumber}")
    @Operation(summary = "Get Person by Tax Number")
    public ResponseEntity<PersonResponseDto> getPersonByTaxNumber(@PathVariable String taxNumber) {
        log.info("GET /api/persons/tax-number/{} - Fetch person with tax number '{}'.", taxNumber, taxNumber);

        PersonResponseDto response = personService.getPersonByTaxNumber(taxNumber);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Person")
    public ResponseEntity<PersonResponseDto> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonRequestDto personRequestDto
    ) {
        log.info("PUT /api/persons/{} - Update person with ID '{}'.", id, id);

        PersonResponseDto response = personService.updatePerson(id, personRequestDto);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Person")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        log.info("DELETE /api/person/{} - Delete person with ID '{}'.", id, id);

        personService.deletePerson(id);

        return ResponseEntity.noContent().build();
    }
}
