package ru.practicum.analyzer.kafka.serialization;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.kafka.serialization.BaseAvroDeserializer;

/**
 * Десериализатор для данных, полученных из топика, хранящего сведения о действиях пользователей.
 */
@Component
public class UserActionAvroDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    /**
     * Конструктор.
     */
    public UserActionAvroDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
