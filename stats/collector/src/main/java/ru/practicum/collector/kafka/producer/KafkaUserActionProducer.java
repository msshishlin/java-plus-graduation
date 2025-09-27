package ru.practicum.collector.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.collector.kafka.configuration.KafkaUserActionProducerConfig;
import ru.practicum.ewm.stats.avro.UserActionAvro;

/**
 * Издатель данных Kafka.
 */
@Component
@Slf4j
public class KafkaUserActionProducer {
    /**
     * Конфигурация издателя данных.
     */
    private final KafkaUserActionProducerConfig config;

    /**
     * Издатель данных.
     */
    private final KafkaProducer<String, UserActionAvro> producer;

    /**
     * Конструктор.
     *
     * @param config конфигурация Kafka.
     */
    public KafkaUserActionProducer(KafkaUserActionProducerConfig config) {
        this.config = config;
        this.producer = new KafkaProducer<>(this.config.getProperties());
    }

    /**
     * Отправить в Kafka данные о действии пользователя.
     *
     * @param userActionAvro данные о действии пользователя.
     */
    public void sendUserAction(UserActionAvro userActionAvro) {
        try {
            producer.send(new ProducerRecord<>(config.getTopic(), userActionAvro));
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }
}
