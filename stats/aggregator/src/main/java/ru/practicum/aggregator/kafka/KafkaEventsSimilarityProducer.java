package ru.practicum.aggregator.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.kafka.configuration.KafkaEventsSimilarityProducerConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

/**
 * Издатель данных Kafka.
 */
@Component
@Slf4j
public class KafkaEventsSimilarityProducer {
    /**
     * Конфигурация издателя данных.
     */
    private final KafkaEventsSimilarityProducerConfig config;

    /**
     * Издатель данных.
     */
    private final KafkaProducer<String, EventSimilarityAvro> producer;

    /**
     * Конструктор.
     *
     * @param config конфигурация издателя данных Kafka.
     */
    public KafkaEventsSimilarityProducer(KafkaEventsSimilarityProducerConfig config) {
        this.config = config;
        this.producer = new KafkaProducer<>(this.config.getProperties());
    }

    /**
     * Отправить значение сходства двух событий в топик Kafka.
     * @param eventSimilarityAvro значение сходства двух событий.
     */
    public void sendEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        try {
            String topic = config.getTopic();
            ProducerRecord<String, EventSimilarityAvro> record = new ProducerRecord<>(topic, eventSimilarityAvro);

            producer.send(record);
        }
        catch(Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }

    public void stop() {
        try {
            producer.flush();
        } finally {
            producer.close();
        }
    }
}
