package ru.practicum.analyzer.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.configuration.KafkaEventsSimilarityConsumerConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;

/**
 * Потребитель данных из топика, хранящего сведения о сходстве событий.
 */
@Component
public class KafkaEventsSimilarityConsumer {
    /**
     * Конфигурация kafka.
     */
    private final KafkaEventsSimilarityConsumerConfig config;

    /**
     * Потребитель данных.
     */
    private final KafkaConsumer<String, EventSimilarityAvro> consumer;

    /**
     * Конструктор.
     *
     * @param config конфигурация потребителя данных Kafka.
     */
    public KafkaEventsSimilarityConsumer(KafkaEventsSimilarityConsumerConfig config) {
        this.config = config;
        this.consumer = new KafkaConsumer<>(this.config.getProperties());

        Runtime.getRuntime().addShutdownHook(new Thread(this.consumer::wakeup));
    }

    /**
     * Метод подписки потребителя данных на топик.
     */
    public void subscribe() {
        consumer.subscribe(List.of(config.getTopic()));
    }

    /**
     * Метод получения данных из топика.
     *
     * @return массив записей.
     */
    public ConsumerRecords<String, EventSimilarityAvro> poll() {
        return consumer.poll(Duration.ofMillis(100));
    }

    /**
     * Остановка потребителя данных.
     */
    public void stop() {
        try {
            consumer.commitSync();
        } finally {
            consumer.close();
        }
    }
}
