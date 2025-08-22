package ru.practicum.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Конфигурация издателя данных Kafka.
 */
@Configuration
@ConfigurationProperties("collector.kafka.producer")
@Getter
@Setter
@ToString
public class KafkaProducerConfig {
    /**
     * Параметры издателя данных.
     */
    private Properties properties;

    /**
     * Коллекция топиков для публикации данных.
     */
    private EnumMap<KafkaTopic, String> topics = new EnumMap<>(KafkaTopic.class);

    /**
     * Конструктор.
     *
     * @param properties параметры издателя данных.
     * @param topics     коллекция топиков для публикации данных.
     */
    public KafkaProducerConfig(Properties properties, Map<String, String> topics) {
        this.properties = properties;

        for (Map.Entry<String, String> entry : topics.entrySet()) {
            String key = entry.getKey().replace("-", "_").toUpperCase();

            if (KafkaTopic.contains(key)) {
                this.topics.put(KafkaTopic.valueOf(key), entry.getValue());
            }
        }
    }
}
