package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonEventConsumer {
    private final PersonService personService;

    @KafkaListener(topics = "person.events", groupId = "person-events-group")
    public void consumePersonEvent(PersonEventDto event) {
        log.info("Consumed person event from Kafka - Action: {}, PersonID: {}", event.getAction(), event.getPersonId());
        try {
            personService.processPersonEvent(event);
            log.info("Successfully processed person event from Kafka");
        } catch (Exception exception) {
            log.error("Error processing consumed event: {}", event, exception);
            // TODO: Add error handling logic (e.g., send to dead-letter topic)
        }
    }
}
