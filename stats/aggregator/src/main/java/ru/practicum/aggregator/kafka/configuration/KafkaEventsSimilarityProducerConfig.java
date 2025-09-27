package ru.practicum.aggregator.kafka.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Конфигурация издателя данных Kafka.
 */
@Configuration
@ConfigurationProperties("aggregator.kafka.producer")
@Getter
@Setter
@ToString
public class KafkaEventsSimilarityProducerConfig {
    /**
     * Параметры потребителя данных.
     */
    private Properties properties;

    /**
     * Топик для публикации данных.
     */
    private String topic;
}
