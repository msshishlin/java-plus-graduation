package ru.practicum.analyzer.model;

import lombok.Data;

/**
 * Составной идентификатор сходства мероприятий.
 */
@Data
public class EventSimilarityId {
    /**
     * Идентификатор первого мероприятия.
     */
    private long eventA;

    /**
     * Идентификатор второго мероприятия.
     */
    private long eventB;
}
