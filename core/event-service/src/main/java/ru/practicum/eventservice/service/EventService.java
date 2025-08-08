package ru.practicum.eventservice.service;

import ru.practicum.eventservice.model.EventSearch;
import ru.practicum.interactionapi.dto.eventservice.CreateEventDto;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.EventShortDto;
import ru.practicum.interactionapi.dto.eventservice.UpdateEventDto;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;

import java.util.Collection;

/**
 * Контракт сервиса для работы с событиями.
 */
public interface EventService {
    /**
     * Добавить новое событие.
     *
     * @param userId         идентификатор текущего пользователя.
     * @param createEventDto трансферный объект, содержащий данные для добавления нового события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto createEvent(Long userId, CreateEventDto createEventDto) throws InvalidEventDateException;

    /**
     * Получить коллекцию событий.
     *
     * @param userId идентификатор пользователя.
     * @param from   количество событий, которое необходимо пропустить.
     * @param size   количество событий, которое необходимо получить.
     * @return коллекция событий.
     */
    Collection<EventDto> getEvents(Long userId, int from, int size);

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
     * Получить событие по его идентификатору.
     *
     * @param userId  идентификатор пользователя.
     * @param eventId идентификатор события.
     * @return событие.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    EventDto getEventById(Long userId, Long eventId) throws AccessToEventForbiddenException, EventNotFoundException;

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
     * @param userId         идентификатор текущего пользователя.
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventEditingException           событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     * @throws InvalidEventDateException       дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto updateEventByUser(Long userId, Long eventId, UpdateEventDto updateEventDto) throws AccessToEventForbiddenException, EventEditingException, EventNotFoundException, InvalidEventDateException;

    /**
     * Обновить событие.
     *
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws EventEditingException     событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException    событие с идентификатором {@code eventId} не найдено.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    EventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) throws EventEditingException, EventNotFoundException, InvalidEventDateException;

    /**
     * Подтвердить участие в событии.
     *
     * @param eventId идентификатор события.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     */
    void confirmParticipation(Long eventId) throws EventNotFoundException;
}
