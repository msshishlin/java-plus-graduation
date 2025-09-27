package ru.practicum.analyzer.kafka.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.consumer.KafkaUserActionConsumer;
import ru.practicum.analyzer.service.UserActionService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

/**
 * Обработчик данных из топика, хранящего сведения о действиях пользователей.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionProcessor {
    /**
     * Потребитель данных из топика, хранящего сведения о действиях пользователей.
     */
    private final KafkaUserActionConsumer consumer;

    /**
     * Сервис для работы с действиями пользователя.
     */
    private final UserActionService userActionService;

    /**
     * Метод для начала процесса агрегации данных.
     */
    public void start() {
        try {
            consumer.subscribe();

            while (true) {
                ConsumerRecords<String, UserActionAvro> records = consumer.poll();
                for (ConsumerRecord<String, UserActionAvro> record : records) {
                    userActionService.save(record.value());
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
