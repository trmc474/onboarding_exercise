package com.netcompany.onboarding_exercise.controllers;

import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.dtos.PersonResponseDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import com.netcompany.onboarding_exercise.templates.CustomApiResponse;
import com.netcompany.onboarding_exercise.utils.filters.MetadataFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/persons")
@Tag(name = "Person", description = "API endpoints for person management.")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @PostMapping
    @Operation(summary = "Create Person")
    public ResponseEntity<CustomApiResponse<PersonResponseDto>> createPerson(@Valid @RequestBody PersonRequestDto personRequestDto) {
        log.info("POST /api/persons - Create Person");

        // Create person
        PersonResponseDto personResponseDto = personService.createPerson(personRequestDto);

        // Create API response
        CustomApiResponse<PersonResponseDto> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.CREATED.value(),
                "Person created successfully.",
                personResponseDto,
                null
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get All Persons")
    public ResponseEntity<CustomApiResponse<List<PersonResponseDto>>> getAllPersons(
            // Pagination parameters
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,

            // Search parameters
            @RequestParam(required = false) String searchField,
            @RequestParam(required = false) String searchValue,

            // Filter parameters
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minTaxDebt,
            @RequestParam(required = false) Double maxTaxDebt,

            // Sorting parameter
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        log.info("GET /api/persons - Get All Persons");

        // Create Sort and Pageable objects
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Fetch all persons
        List<PersonResponseDto> personResponseDtos =
                personService.getAllPersons(searchField, searchValue, minAge, maxAge, minTaxDebt, maxTaxDebt, pageable);

        // Create metadata
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("pageNumber", pageNumber);
        metadata.put("pageSize", pageSize);
        metadata.put("searchField", searchField);
        metadata.put("searchValue", searchValue);
        metadata.put("minAge", minAge);
        metadata.put("maxAge", maxAge);
        metadata.put("minTaxDebt", minTaxDebt);
        metadata.put("maxTaxDebt", maxTaxDebt);
        metadata.put("sortBy", sortBy);
        metadata.put("sortDirection", sortDirection);
        metadata = MetadataFilter.filterMetadata(metadata);

        // Create API response
        CustomApiResponse<List<PersonResponseDto>> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "All persons fetched successfully.",
                personResponseDtos,
                metadata
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/search/mi-over-30")
    @Operation(summary = "Get Persons Whose Name Starting with 'Mi' and Older Than 30.")
    public ResponseEntity<CustomApiResponse<List<PersonResponseDto>>> getPersonsWithMiAndOlderThan30(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        log.info("GET /api/persons/search/mi-over-30 - Get Persons Whose Name Starting with 'Mi' and Older Than 30");

        // Create Sort and Pageable objects
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        List<PersonResponseDto> personResponseDtos = personService.getPersonsWithMiAndOlderThan30(pageable);

        // Create metadata
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("pageNumber", pageNumber);
        metadata.put("pageSize", pageSize);
        metadata.put("sortBy", sortBy);
        metadata.put("sortDirection", sortDirection);
        metadata = MetadataFilter.filterMetadata(metadata);

        // Create API response
        CustomApiResponse<List<PersonResponseDto>> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Persons whose name starting with 'Mi' and older than 30 fetched successfully.",
                personResponseDtos,
                metadata
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Person By ID")
    public ResponseEntity<CustomApiResponse<PersonResponseDto>> getPersonById(@PathVariable Long id) {
        log.info("GET /api/persons/{} - Fetch Person By ID", id);

        // Fetch person by ID
        PersonResponseDto personResponseDto = personService.getPersonById(id);

        // Create API response
        CustomApiResponse<PersonResponseDto> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Person with ID '" + id + "' fetched successfully.",
                personResponseDto,
                null
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/tax-number/{taxNumber}")
    @Operation(summary = "Get Person By Tax Number")
    public ResponseEntity<CustomApiResponse<PersonResponseDto>> getPersonByTaxNumber(@PathVariable String taxNumber) {
        log.info("GET /api/persons/tax-number/{} - Fetch Person By Tax Number", taxNumber);

        // Fetch person by tax number
        PersonResponseDto personResponseDto = personService.getPersonByTaxNumber(taxNumber);

        // Create API response
        CustomApiResponse<PersonResponseDto> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Person with tax number '" + taxNumber + "' fetched successfully.",
                personResponseDto,
                null
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Person")
    public ResponseEntity<CustomApiResponse<PersonResponseDto>> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonRequestDto personRequestDto
    ) {
        log.info("PUT /api/persons/{} - Update Person", id);

        // Update person
        PersonResponseDto personResponseDto = personService.updatePerson(id, personRequestDto);

        // Create API response
        CustomApiResponse<PersonResponseDto> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.OK.value(),
                "Person with ID '" + id + "' updated successfully.",
                personResponseDto,
                null
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Person")
    public ResponseEntity<CustomApiResponse<Void>> deletePerson(@PathVariable Long id) {
        log.info("DELETE /api/person/{} - Delete Person", id);

        // Delete person
        personService.deletePerson(id);

        // Create API response
        CustomApiResponse<Void> apiResponse = new CustomApiResponse<>(
                true,
                HttpStatus.NO_CONTENT.value(),
                "Person with ID '" + id + "' deleted successfully.",
                null,
                null
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }
}
