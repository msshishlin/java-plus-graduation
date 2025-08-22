package ru.practicum.collector.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.collector.configuration.KafkaProducerConfig;
import ru.practicum.collector.configuration.KafkaTopic;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
@Slf4j
public class KafkaStatsProducer {
    /**
     * Конфигурация издателя данных.
     */
    private final KafkaProducerConfig config;

    /**
     * Издатель данных.
     */
    private final KafkaProducer<String, SpecificRecordBase> producer;

    /**
     * Конструктор.
     *
     * @param config конфигурация Kafka.
     */
    public KafkaStatsProducer(KafkaProducerConfig config) {
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
            String topic = config.getTopics().get(KafkaTopic.USER_ACTIONS);
            ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, userActionAvro);

            producer.send(record);
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }
}
