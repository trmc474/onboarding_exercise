package com.netcompany.onboarding_exercise.config;

import com.netcompany.onboarding_exercise.dtos.PersonEventDto;
import com.netcompany.onboarding_exercise.dtos.TaxCalculationEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, PersonEventDto> personEventConsumerFactory() {
        JsonDeserializer<PersonEventDto> deserializer = new JsonDeserializer<>(PersonEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> config = baseConsumerConfig();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "person-events-group");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PersonEventDto> personEventSingleContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PersonEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(personEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PersonEventDto> personEventContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PersonEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Use separate consumer factory for batch processing
        Map<String, Object> batchConfig = baseConsumerConfig();
        batchConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "person-events-batch-group");
        batchConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        batchConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);

        JsonDeserializer<PersonEventDto> deserializer = new JsonDeserializer<>(PersonEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        ConsumerFactory<String, PersonEventDto> batchConsumerFactory =
                new DefaultKafkaConsumerFactory<>(batchConfig, new StringDeserializer(), deserializer);

        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, TaxCalculationEventDto> taxEventConsumerFactory() {
        JsonDeserializer<TaxCalculationEventDto> deserializer = new JsonDeserializer<>(TaxCalculationEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> config = baseConsumerConfig();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "tax-calculation-group");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaxCalculationEventDto> taxEventContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaxCalculationEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taxEventConsumerFactory());
        return factory;
    }
}
