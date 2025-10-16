package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonEventBatchConsumer {
    private static final String RETRY_TOPIC = "person.events.batch.retry";
    private final PersonService personService;
    private final KafkaTemplate<String, PersonEventDto> kafkaTemplate;

    @Transactional
    @KafkaListener(topics = "person.events.batch", containerFactory = "personEventContainerFactory")
    public void consumePersonEventBatch(List<PersonEventDto> events, Acknowledgment acknowledgment) {
        log.info("Batch received: Processing {} person events", events.size());

        boolean isDependentProcessing =
                events.stream().anyMatch(event -> event.getProcessingMode() == PersonEventDto.ProcessingMode.DEPENDENT);

        if (isDependentProcessing) {
            processDependentBatch(events, acknowledgment);
        } else {
            processIndependentBatch(events, acknowledgment);
        }
    }


    public void processDependentBatch(List<PersonEventDto> events, Acknowledgment acknowledgment) {
        log.info("Processing batch in DEPENDENT mode (blocking retry)");

        try {
            for (int i = 0; i < events.size(); i++) {
                PersonEventDto event = events.get(i);
                log.info("Processing dependent event {}/{}: Action={}", i + 1, events.size(), event.getAction());

                if (shouldSimulateFailure(event)) {
                    log.error("DEPENDENT batch failure at position {}. Rejecting entire batch", i + 1);
                    throw new RuntimeException("Simulated dependent processing failure at position " + (i + 1));
                }

                personService.processPersonEvent(event);
                log.debug("Successfully processed dependent event {}/{}", i + 1, events.size());
            }

            acknowledgment.acknowledge();
            log.info("DEPENDENT batch acknowledged: {} events processed successfully", events.size());

        } catch (Exception e) {
            log.error("DEPENDENT batch failed: Entire batch will be redelivered", e);
            throw new RuntimeException("Dependent batch processing failed", e);
        }
    }

    public void processIndependentBatch(List<PersonEventDto> events, Acknowledgment acknowledgment) {
        log.info("Processing batch in INDEPENDENT mode (non-blocking retry)");

        List<PersonEventDto> failedEvents = new ArrayList<>();
        int successCount = 0;

        for (int i = 0; i < events.size(); i++) {
            PersonEventDto event = events.get(i);

            try {
                log.info("Processing independent event {}/{}: Action={}", i + 1, events.size(), event.getAction());

                if (shouldSimulateFailure(event)) {
                    log.warn("INDEPENDENT event failure at position {}. Adding to retry queue", i + 1);
                    throw new RuntimeException("Simulated independent processing failure at position " + (i + 1));
                }

                personService.processPersonEvent(event);
                successCount++;
                log.debug("Successfully processed independent event {}/{}", i + 1, events.size());

            } catch (Exception e) {
                log.warn("Independent event {} failed. Adding to retry queue", i + 1, e);
                failedEvents.add(event);
            }
        }

        sendFailedEventsToRetry(failedEvents);
        acknowledgment.acknowledge();
        log.info(
                "INDEPENDENT batch acknowledged: {} successful, {} failed (sent to retry)",
                successCount,
                failedEvents.size()
        );
    }

    private void sendFailedEventsToRetry(List<PersonEventDto> failedEvents) {
        failedEvents.forEach(event -> {
            log.info("Sending failed event to retry topic: Action={}", event.getAction());
            kafkaTemplate.send(RETRY_TOPIC, event);
        });
    }

    private boolean shouldSimulateFailure(PersonEventDto event) {
        return event.getPersonData() != null && "batch-fail".equalsIgnoreCase(event.getPersonData().getFirstName());
    }
}
