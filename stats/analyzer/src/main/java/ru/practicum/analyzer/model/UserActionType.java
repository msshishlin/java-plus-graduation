package ru.practicum.analyzer.model;

/**
 * Тип действия пользователя.
 */
public enum UserActionType {
    /**
     * Просмотр страницы мероприятия.
     */
    VIEW,

    /**
     * Заявка на участие в мероприятии.
     */
    REGISTER,

    /**
     * Положительная оценка/лайк мероприятию.
     */
    LIKE
}