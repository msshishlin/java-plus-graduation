package ru.practicum.analyzer.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.configuration.KafkaUserActionConsumerConfig;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

/**
 * Потребитель данных из топика, хранящего сведения о действиях пользователей.
 */
@Component
public class KafkaUserActionConsumer {
    /**
     * Конфигурация kafka.
     */
    private final KafkaUserActionConsumerConfig config;

    /**
     * Потребитель данных.
     */
    private final KafkaConsumer<String, UserActionAvro> consumer;

    /**
     * Конструктор.
     *
     * @param config конфигурация потребителя данных Kafka.
     */
    public KafkaUserActionConsumer(KafkaUserActionConsumerConfig config) {
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
    public ConsumerRecords<String, UserActionAvro> poll() {
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
