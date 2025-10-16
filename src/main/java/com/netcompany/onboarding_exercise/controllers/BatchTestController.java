package com.netcompany.onboarding_exercise.controllers;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.dtos.PersonRequestDto;
import com.netcompany.onboarding_exercise.templates.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/test/batch")
@Tag(name = "Batch Retry Testing", description = "Professional endpoints for testing batch retry mechanisms")
@RequiredArgsConstructor
@Profile("!production")
public class BatchTestController {

    private final KafkaTemplate<String, PersonEventDto> personKafkaTemplate;

    @PostMapping("/send-person-batch")
    @Operation(summary = "Send batch with configurable retry strategy")
    public ResponseEntity<CustomApiResponse<String>> sendPersonEventBatch(
            @RequestParam(defaultValue = "5") int batchSize,
            @RequestParam(defaultValue = "-1") int failurePosition,
            @RequestParam(defaultValue = "INDEPENDENT") PersonEventDto.ProcessingMode processingMode
    ) {
        String batchId = UUID.randomUUID().toString().substring(0, 8);
        log.info(
                "Sending batch '{}': size={}, failure at position={}, mode={}",
                batchId,
                batchSize,
                failurePosition,
                processingMode
        );

        for (int i = 0; i < batchSize; i++) {
            PersonRequestDto personData = new PersonRequestDto();
            boolean isFailingMessage = (i == failurePosition);

            personData.setFirstName(isFailingMessage ? "batch-fail" : "BatchUser");
            personData.setLastName(String.format("Test-%s-%d", batchId, i));
            personData.setDateOfBirth(LocalDate.of(1990, 1, (i % 28) + 1));
            personData.setTaxNumber(String.format("BATCH-%s-%03d", batchId, i));

            PersonEventDto event = PersonEventDto.builder()
                    .action(PersonEventDto.Action.CREATE)
                    .personData(personData)
                    .processingMode(processingMode)
                    .build();

            personKafkaTemplate.send("person.events.batch", event);
        }

        String message = String.format(
                "Batch '%s' sent: %d events, failure at position %d, mode: %s",
                batchId,
                batchSize,
                failurePosition,
                processingMode
        );

        CustomApiResponse<String> response =
                new CustomApiResponse<>(true, HttpStatus.ACCEPTED.value(), message, batchId, null);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/dependent-batch")
    @Operation(summary = "Send dependent batch (blocking retry)")
    public ResponseEntity<CustomApiResponse<String>> sendDependentBatch(
            @RequestParam(defaultValue = "3") int batchSize,
            @RequestParam(defaultValue = "1") int failurePosition
    ) {

        return sendPersonEventBatch(batchSize, failurePosition, PersonEventDto.ProcessingMode.DEPENDENT);
    }

    @PostMapping("/independent-batch")
    @Operation(summary = "Send independent batch (non-blocking retry)")
    public ResponseEntity<CustomApiResponse<String>> sendIndependentBatch(
            @RequestParam(defaultValue = "5") int batchSize,
            @RequestParam(defaultValue = "2") int failurePosition
    ) {

        return sendPersonEventBatch(batchSize, failurePosition, PersonEventDto.ProcessingMode.INDEPENDENT);
    }
}
