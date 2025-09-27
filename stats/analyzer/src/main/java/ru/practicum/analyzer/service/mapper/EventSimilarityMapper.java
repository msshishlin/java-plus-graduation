package ru.practicum.analyzer.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

/**
 * Маппер для сущности сходства мероприятий.
 */
@Component
public class EventSimilarityMapper {
    /**
     * Преобразовать объект сходства мероприятий в формате Avro в объект сущности сходства мероприятий.
     *
     * @param eventSimilarityAvro объект сходства мероприятий в формате Avro.
     * @return объект сущности сходства мероприятий.
     */
    public EventSimilarity mapToEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        return EventSimilarity.builder()
                .eventA(eventSimilarityAvro.getEventA())
                .eventB(eventSimilarityAvro.getEventB())
                .score(eventSimilarityAvro.getScore())
                .timestamp(eventSimilarityAvro.getTimestamp())
                .build();
    }
}
