package ru.practicum.analyzer.kafka.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Конфигурация потребителя данных из топика, хранящего сведения о сходстве событий.
 */
@Configuration
@ConfigurationProperties("analyzer.kafka.events-similarity-consumer")
@Getter
@Setter
@ToString
public class KafkaEventsSimilarityConsumerConfig {
    /**
     * Параметры потребителя данных.
     */
    private Properties properties;

    /**
     * Топик для получения данных.
     */
    private String topic;
}
