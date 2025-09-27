package ru.practicum.aggregator.kafka.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Конфигурация потребителя данных Kafka.
 */
@Configuration
@ConfigurationProperties("aggregator.kafka.consumer")
@Getter
@Setter
@ToString
public class KafkaUserActionConsumerConfig {
    /**
     * Параметры потребителя данных.
     */
    private Properties properties;

    /**
     * Топик для получения данных.
     */
    private String topic;
}
