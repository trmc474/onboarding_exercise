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
public class BatchRetryConsumer {
    private final PersonService personService;

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 3000, multiplier = 2.0), autoCreateTopics = "true",
            numPartitions = "1", replicationFactor = "1", topicSuffixingStrategy =
            TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, include = {
            DataAccessException.class, RuntimeException.class
    })
    @KafkaListener(topics = "person.events.batch.retry", containerFactory = "personEventSingleContainerFactory")
    public void consumeRetryEvent(PersonEventDto event) {
        log.info("RETRY CONSUMER: Processing failed event - Action: {}", event.getAction());

        try {
            personService.processPersonEvent(event);
            log.info("RETRY SUCCESS: Event processed successfully on retry");
        } catch (Exception exception) {
            log.error("RETRY FAILED: Event will retry again or go to DLT", exception);
            throw exception;
        }
    }

    @DltHandler
    public void handleRetryDlt(PersonEventDto event) {
        log.error("RETRY DLT: Event moved to Dead Letter Topic after all retries failed: {}", event);
    }
}
