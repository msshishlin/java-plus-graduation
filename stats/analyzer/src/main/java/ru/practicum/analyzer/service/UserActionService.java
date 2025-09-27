package ru.practicum.analyzer.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

/**
 * Контракт сервиса для работы с действиями пользователя.
 */
public interface UserActionService {
    /**
     * Сохранить действие пользователя.
     *
     * @param userActionAvro действие пользователя в формате Avro.
     */
    void save(UserActionAvro userActionAvro);
}
