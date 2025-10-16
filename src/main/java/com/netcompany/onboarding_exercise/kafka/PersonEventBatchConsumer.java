package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonEventBatchConsumer {
    private final PersonService personService;

    @KafkaListener(topics = "person.events.batch", containerFactory = "personEventContainerFactory")
    public void consumePersonEventBatch(List<PersonEventDto> events, Acknowledgment acknowledgment) {
        log.info("=== BATCH RECEIVED: Processing {} person events ===", events.size());

        try {
            processEventBatch(events);

            acknowledgment.acknowledge();
            log.info("=== BATCH ACKNOWLEDGED: Successfully processed {} events ===", events.size());

        } catch (Exception exception) {
            log.error("=== BATCH FAILED: Error processing batch. Will be redelivered ===", exception);
        }
    }

    private void processEventBatch(List<PersonEventDto> events) {
        for (int i = 0; i < events.size(); i++) {
            PersonEventDto event = events.get(i);

            log.info(
                    "Processing event {}/{}: Action={}, PersonID={}",
                    i + 1,
                    events.size(),
                    event.getAction(),
                    event.getPersonId()
            );

            // Simulate batch processing error for testing
            if (event.getPersonData() != null && "batch-fail".equalsIgnoreCase(event.getPersonData().getFirstName())) {
                log.error("!!! SIMULATING BATCH ERROR at position {} !!!", i + 1);
                throw new RuntimeException("Simulated batch processing failure at position " + (i + 1));
            }

            try {
                personService.processPersonEvent(event);
                log.debug("Successfully processed event {}/{}", i + 1, events.size());
            } catch (Exception exception) {
                log.error("Error processing event {}/{} in batch", i + 1, events.size(), exception);
                throw exception;
            }
        }
    }
}
