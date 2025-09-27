package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.Collection;

/**
 * Контракт хранилища данных о сходствах мероприятий.
 */
public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    /**
     * Найти все сходства первого или второго мероприятий.
     *
     * @param eventA идентификатор первого мероприятия.
     * @param eventB идентификатор второго мероприятия.
     * @return сходства мероприятий.
     */
    Collection<EventSimilarity> findByEventAOrEventB(long eventA, long eventB);
}
