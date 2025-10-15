package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonEventProducer {
    private static final String TOPIC_NAME = "person.events";
    private final KafkaTemplate<String, PersonEventDto> kafkaTemplate;

    public void sendPersonEvent(PersonEventDto event) {
        log.info("Sending person event to Kafka - Action: {}, PersonID: {}", event.getAction(), event.getPersonId());
        try {
            kafkaTemplate.send(TOPIC_NAME, event);
            log.info("Successfully sent event to Kafka topic: {}.", TOPIC_NAME);
        } catch (Exception exception) {
            log.error("Failed to send person event to Kafka.", exception);
            throw new RuntimeException("Failed to send event to Kafka.", exception);
        }
    }
}
