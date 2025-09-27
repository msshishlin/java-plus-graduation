package ru.practicum.aggregator.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.handler.UserActionHandler;
import ru.practicum.aggregator.kafka.KafkaEventsSimilarityProducer;
import ru.practicum.aggregator.kafka.KafkaUserActionConsumer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationProcessor {
    /**
     * Потребитель данных Kafka.
     */
    private final KafkaUserActionConsumer consumer;

    /**
     * Издатель данных Kafka.
     */
    private final KafkaEventsSimilarityProducer producer;

    /**
     * Обработчик действий пользователей над мероприятиями.
     */
    private final UserActionHandler userActionHandler;

    /**
     * Метод для начала процесса агрегации данных.
     */
    public void start() {
        try {
            consumer.subscribe();

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll();

                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    Optional<List<EventSimilarityAvro>> eventSimilarityAvroListOptional = userActionHandler.handle(record.value());
                    if (eventSimilarityAvroListOptional.isEmpty()) {
                        continue;
                    }

                    for (EventSimilarityAvro eventSimilarityAvro : eventSimilarityAvroListOptional.get()) {
                        producer.sendEventSimilarity(eventSimilarityAvro);
                    }
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            consumer.stop();
            producer.stop();
        }
    }
}
