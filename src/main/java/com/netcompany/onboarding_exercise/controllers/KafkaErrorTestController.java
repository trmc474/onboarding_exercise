package com.netcompany.onboarding_exercise.controllers;

import com.netcompany.onboarding_exercise.dtos.TaxCalculationEventDto;
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

@Slf4j
@RestController
@RequestMapping("/api/test/kafka")
@Tag(name = "Kafka Error Testing", description = "Professional endpoints for testing Kafka error handling")
@RequiredArgsConstructor
@Profile("!production")
public class KafkaErrorTestController {

    private final KafkaTemplate<String, TaxCalculationEventDto> kafkaTemplate;

    @PostMapping("/simulate-tax-error")
    @Operation(summary = "Simulate tax calculation error for testing retry mechanism")
    public ResponseEntity<CustomApiResponse<Void>> simulateTaxError() {
        log.info("Simulating tax calculation error for testing");

        // Send a tax event with a non-existent tax number to trigger error
        TaxCalculationEventDto failEvent = new TaxCalculationEventDto("NON_EXISTENT_TAX", 100.0);

        kafkaTemplate.send("tax.calculation", failEvent);

        CustomApiResponse<Void> response = new CustomApiResponse<>(
                true,
                HttpStatus.ACCEPTED.value(),
                "Tax error simulation event sent. Check logs for retry behavior.",
                null,
                null
        );

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/send-valid-tax-event")
    @Operation(summary = "Send valid tax calculation event")
    public ResponseEntity<CustomApiResponse<Void>> sendValidTaxEvent(
            @RequestParam String taxNumber,
            @RequestParam Double amount
    ) {

        log.info("Sending valid tax calculation event - taxNumber: {}, amount: {}", taxNumber, amount);

        TaxCalculationEventDto event = new TaxCalculationEventDto(taxNumber, amount);
        kafkaTemplate.send("tax.calculation", event);

        CustomApiResponse<Void> response = new CustomApiResponse<>(
                true,
                HttpStatus.ACCEPTED.value(),
                "Valid tax calculation event sent successfully.",
                null,
                null
        );

        return ResponseEntity.accepted().body(response);
    }
}
