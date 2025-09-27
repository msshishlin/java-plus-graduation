package ru.practicum.analyzer.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

/**
 * Контракт сервиса для работы со сходством мероприятий
 */
public interface EventSimilarityService {
    /**
     * Сохранить сходство мероприятий.
     *
     * @param eventSimilarityAvro сходство мероприятий в формате Avro.
     */
    void save(EventSimilarityAvro eventSimilarityAvro);
}
