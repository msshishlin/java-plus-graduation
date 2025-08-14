package ru.practicum.requestservice.service;

import ru.practicum.interactionapi.dto.requestservice.RequestDto;
import ru.practicum.interactionapi.dto.requestservice.RequestsStatusDto;
import ru.practicum.interactionapi.dto.requestservice.UpdateEventRequestsStatusDto;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.requestservice.CancelRequestException;
import ru.practicum.interactionapi.exception.requestservice.CreateRequestException;
import ru.practicum.interactionapi.exception.requestservice.RequestNotFoundException;
import ru.practicum.interactionapi.exception.requestservice.UpdateRequestStatusException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;

import java.util.Collection;

/**
 * Контракт сервиса для работы с заявками на участие в событиях.
 */
public interface RequestService {
    /**
     * Создать заявку на участие текущего пользователя в событии.
     *
     * @param requesterId идентификатор пользователя, оставившего заявку.
     * @param eventId     идентификатор события.
     * @return заявка на участие в событии.
     * @throws CreateRequestException невозможно создать заявку на участие в событии.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     * @throws UserNotFoundException  пользователь с идентификатором {@code userId} не найден.
     */
    RequestDto createRequest(Long requesterId, Long eventId) throws CreateRequestException, EventNotFoundException, UserNotFoundException;

    /**
     * Получить коллекцию заявок пользователя на участие в событиях.
     *
     * @param requesterId идентификатор пользователя, оставившего заявки.
     * @return коллекция заявок на участие в событиях.
     */
    Collection<RequestDto> getUserRequests(Long requesterId);

    /**
     * Получить заявки на участие в событии, инициированным текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора события.
     * @param eventId     идентификатор события.
     * @return заявка на участие в событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    Collection<RequestDto> getEventRequests(Long initiatorId, Long eventId) throws AccessToEventForbiddenException, EventNotFoundException;

    /**
     * Обновить статус заявок на участие в событии, инициированным текущим пользователем.
     *
     * @param initiatorId                  идентификатор инициатора события.
     * @param eventId                      идентификатор события.
     * @param updateEventRequestsStatusDto трансферный объект, содержащий данные для обновления статусов заявок.
     * @return информация о подтвержденных и отклоненных заявках на участие в событии пользователя.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     * @throws UserNotFoundException           пользователь с идентификатором {@code userId} не найден.
     */
    RequestsStatusDto updateEventRequestsStatus(Long initiatorId, Long eventId, UpdateEventRequestsStatusDto updateEventRequestsStatusDto) throws AccessToEventForbiddenException, EventNotFoundException, UpdateRequestStatusException;

    /**
     * Отменить заявку на участие пользователя в событии.
     *
     * @param requesterId идентификатор пользователя, оставившего заявку.
     * @param requestId   идентификатор заявки.
     * @return заявка на участие в событии.
     * @throws CancelRequestException   невозможно отменить заявку на участие в событии.
     * @throws RequestNotFoundException заявка с идентификатором {@code requestId} не найдена.
     */
    RequestDto cancelUserRequest(Long requesterId, Long requestId) throws CancelRequestException, RequestNotFoundException;
}
