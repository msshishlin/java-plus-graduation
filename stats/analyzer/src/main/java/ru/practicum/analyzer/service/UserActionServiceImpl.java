package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.analyzer.service.mapper.UserActionMapper;
import ru.practicum.ewm.stats.avro.UserActionAvro;

/**
 * Сервис для работы с действиями пользователя.
 */
@RequiredArgsConstructor
@Service
public class UserActionServiceImpl implements UserActionService {
    /**
     * Хранилище данных о действиях пользователей над мероприятиями.
     */
    private final UserActionRepository userActionRepository;

    /**
     * Маппер для сущности действия пользователя.
     */
    private final UserActionMapper userActionMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserActionAvro userActionAvro) {
        userActionRepository.save(userActionMapper.mapToUserAction(userActionAvro));
    }
}
