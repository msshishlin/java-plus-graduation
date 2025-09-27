package ru.practicum.collector.kafka.configuration;

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
@ConfigurationProperties("collector.kafka.producer")
@Getter
@Setter
@ToString
public class KafkaUserActionProducerConfig {
    /**
     * Параметры издателя данных.
     */
    private Properties properties;

    /**
     * Топик для публикации данных.
     */
    private String topic;
}
