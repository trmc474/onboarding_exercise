package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.TaxCalculationEventDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxEventConsumer {
    private final PersonService personService;

    @KafkaListener(topics = "tax.calculation", containerFactory = "taxEventContainerFactory")
    public void consumeTaxCalculationEvent(TaxCalculationEventDto event) {
        log.info(
                "Consumed TaxCalculationEvent from Kafka - tax number: {}, amount: {}",
                event.getTaxNumber(),
                event.getAmount()
        );

        try {
            personService.updateTaxDebt(event.getTaxNumber(), event.getAmount());
            log.info("Successfully processed TaxCalculationEvent for tax number: {}", event.getTaxNumber());
        } catch (Exception e) {
            log.error("Error processing TaxCalculationEvent for tax number: {}", event.getTaxNumber(), e);
            // TODO: Add error handling logic (e.g., send to dead-letter topic, retry mechanism)
        }
    }
}
