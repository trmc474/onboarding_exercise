package com.netcompany.onboarding_exercise.kafka;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
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
public class PersonEventConsumer {
    private final PersonService personService;

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, include = {
            DataAccessException.class, RuntimeException.class
    })
    @KafkaListener(topics = "person.events", containerFactory = "personEventSingleContainerFactory")
    public void consumePersonEvent(PersonEventDto event) {
        log.info("Processing single person event - Action: {}, PersonID: {}", event.getAction(), event.getPersonId());

        try {
            personService.processPersonEvent(event);
            log.info("Successfully processed single person event");
        } catch (Exception exception) {
            log.error("Error processing single person event. Will retry if retriable", exception);
            throw exception;
        }
    }

    @DltHandler
    public void handlePersonEventDlt(PersonEventDto event) {
        log.error("Person event moved to Dead Letter Topic: {}", event);
    }
}
