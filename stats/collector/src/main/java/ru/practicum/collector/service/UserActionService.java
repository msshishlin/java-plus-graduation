package ru.practicum.collector.service;


import stats.message.collector.UserActionProto;

/**
 * Контракт сервиса для работы с информацией о действиях пользователей.
 */
public interface UserActionService {
    /**
     * Обработать информацию о действии пользователя.
     *
     * @param userActionProto информация о действии пользователя.
     */
    void handle(UserActionProto userActionProto);
}
