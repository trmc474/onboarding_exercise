package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.TaxCalculationEventDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxEventConsumer {
    private final PersonService personService;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000, multiplier = 1.5), autoCreateTopics = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, include = {
            DataAccessException.class, RuntimeException.class
    })
    @KafkaListener(topics = "tax.calculation", containerFactory = "taxEventContainerFactory")
    public void consumeTaxCalculationEvent(TaxCalculationEventDto event) {
        log.info(
                "Processing tax calculation event - taxNumber: {}, amount: {}",
                event.getTaxNumber(),
                event.getAmount()
        );

        try {
            personService.updateTaxDebt(event.getTaxNumber(), event.getAmount());
            log.info("Successfully processed tax calculation for taxNumber: {}", event.getTaxNumber());
        } catch (Exception exception) {
            log.error(
                    "Error processing tax calculation. Will retry if retriable: {}",
                    exception.getMessage(),
                    exception
            );
            throw exception;
        }
    }

    @DltHandler
    public void handleTaxCalculationDlt(TaxCalculationEventDto event) {
        log.error("Tax calculation event moved to Dead Letter Topic after all retries failed: {}", event);
    }
}
