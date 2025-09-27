package ru.practicum.analyzer.kafka.serialization;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.kafka.serialization.BaseAvroDeserializer;

/**
 * Десериализатор для данных, полученных из топика, хранящего сведения о сходстве событий.
 */
@Component
public class EventSimilarityAvroDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    /**
     * Конструктор.
     */
    public EventSimilarityAvroDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}
