package ru.practicum.eventservice.service;

import ru.practicum.eventservice.model.EventSearch;
import ru.practicum.interactionapi.dto.eventservice.CreateEventDto;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.EventShortDto;
import ru.practicum.interactionapi.dto.eventservice.UpdateEventDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;

import java.util.Collection;

/**
 * Контракт сервиса для работы с событиями.
 */
public interface EventService {
    /**
     * Добавить новое событие.
     *
     * @param initiatorId    идентификатор инициатора события.
     * @param createEventDto трансферный объект, содержащий данные для добавления нового события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto createEvent(Long initiatorId, CreateEventDto createEventDto) throws InvalidEventDateException;

    /**
     * Получить коллекцию событий, добавленных текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора событий.
     * @param from        количество событий, которое необходимо пропустить.
     * @param size        количество событий, которое необходимо получить.
     * @return трансферный объект, содержащий данные о событии.
     * @throws UserNotFoundException пользователь с идентификатором {@code initiatorId} не найден.
     */
    Collection<EventDto> getEvents(Long initiatorId, int from, int size) throws UserNotFoundException;

    /**
     * Получить коллекцию событий.
     *
     * @param eventIds идентификаторы событий.
     * @return коллекция трансферных объектов, содержащий краткую информацию по событиям.
     */
    Collection<EventShortDto> getEvents(Collection<Long> eventIds);

    /**
     * Получить коллекцию событий.
     *
     * @param search объект, содержащий параметры поиска событий.
     * @return коллекция трансферных объектов, содержащий краткую информацию по событиям.
     */
    Collection<EventDto> getEvents(EventSearch search);

    /**
     * Получить коллекцию событий.
     *
     * @param search объект, содержащий параметры поиска событий.
     * @return коллекция трансферных объектов, содержащий краткую информацию по событиям.
     */
    Collection<EventShortDto> getPublishedEvents(EventSearch search);

    /**
     * Получить событие, добавленное текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора события.
     * @param eventId     идентификатор события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    EventDto getEvent(Long initiatorId, Long eventId) throws AccessToEventForbiddenException, EventNotFoundException;

    /**
     * Получить опубликованное событие по его идентификатору.
     *
     * @param eventId идентификатор события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     */
    EventDto getPublishedEventById(Long eventId) throws EventNotFoundException;

    /**
     * Обновить событие.
     *
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws CategoryNotFoundException категория с идентификатором {@code updateEventDto.category} не найдена.
     * @throws EventEditingException     событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException    событие с идентификатором {@code eventId} не найдено.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto updateEvent(Long eventId, UpdateEventDto updateEventDto) throws CategoryNotFoundException, EventEditingException, EventNotFoundException, InvalidEventDateException;

    /**
     * Обновить событие, добавленное текущим пользователем.
     *
     * @param initiatorId    идентификатор инициатора события.
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws CategoryNotFoundException       категория с идентификатором {@code updateEventDto.category} не найдена.
     * @throws EventEditingException           событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     * @throws InvalidEventDateException       дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto updateEvent(Long initiatorId, Long eventId, UpdateEventDto updateEventDto) throws AccessToEventForbiddenException, CategoryNotFoundException, EventEditingException, EventNotFoundException, InvalidEventDateException;

    /**
     * Проверить существует ли событие.
     *
     * @param eventId идентификатор события.
     * @return признак существует ли событие.
     */
    boolean isEventExists(Long eventId);

    /**
     * Проверить существуют ли события с данной категорией.
     *
     * @param categoryId идентификатор категории.
     * @return признак существуют ли события с данной категорией.
     */
    boolean isEventsWithCategoryExists(Long categoryId);

    /**
     * Проверить опубликовано ли событие.
     *
     * @param eventId идентификатор события.
     * @return признак, опубликовано ли событие.
     */
    boolean isEventPublished(Long eventId) throws EventNotFoundException;

    /**
     * Подтвердить участие в событии.
     *
     * @param eventId идентификатор события.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     */
    void confirmParticipation(Long eventId) throws EventNotFoundException;

    /**
     * Отменить участие в событии.
     *
     * @param eventId идентификатор события.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     */
    void rejectParticipation(Long eventId) throws EventNotFoundException;
}
