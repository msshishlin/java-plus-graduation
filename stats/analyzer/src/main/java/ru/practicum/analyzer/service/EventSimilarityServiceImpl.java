package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.service.mapper.EventSimilarityMapper;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

/**
 * Сервис для работы со сходством мероприятий.
 */
@RequiredArgsConstructor
@Service
public class EventSimilarityServiceImpl implements EventSimilarityService {
    /**
         * Хранилище данных о сходствах мероприятий.
     */
    private final EventSimilarityRepository eventSimilarityRepository;

    /**
     * Маппер для сущности сходства мероприятий.
     */
    private final EventSimilarityMapper eventSimilarityMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EventSimilarityAvro eventSimilarityAvro) {
        eventSimilarityRepository.save(eventSimilarityMapper.mapToEventSimilarity(eventSimilarityAvro));
    }
}
