package ru.practicum.analyzer.kafka.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.consumer.KafkaEventsSimilarityConsumer;
import ru.practicum.analyzer.service.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

/**
 * Обработчик данных из топика, хранящего сведения о сходстве событий.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventSimilarityProcessor implements Runnable {
    /**
     * Потребитель данных из топика, хранящего сведения о сходстве событий.
     */
    private final KafkaEventsSimilarityConsumer consumer;

    /**
     * Сервис для работы со сходством мероприятий.
     */
    private final EventSimilarityService eventSimilarityService;

    /**
     * Метод для начала процесса агрегации данных.
     */
    public void run() {
        try {
            consumer.subscribe();

            while (true) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll();
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    eventSimilarityService.save(record.value());
                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            consumer.stop();
        }
    }
}
