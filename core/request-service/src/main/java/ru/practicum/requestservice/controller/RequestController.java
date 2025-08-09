package ru.practicum.requestservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
import ru.practicum.requestservice.service.RequestService;

import java.util.Collection;

/**
 * Контроллер для работы с заявками на участие в событиях.
 */
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RequestController {
    /**
     * Сервис для работы с заявками на участие в событиях.
     */
    private final RequestService requestService;

    /**
     * Создать заявку на участие текущего пользователя в событии.
     *
     * @param requesterId идентификатор пользователя, оставившего заявку.
     * @param eventId     идентификатор события.
     * @return заявка на участие в событии.
     * @throws CreateRequestException невозможно создать заявку на участие в событии.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     * @throws UserNotFoundException  пользователь с идентификатором {@code requesterId} не найден.
     */
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable(name = "userId") @Positive Long requesterId,
                                    @RequestParam @Positive Long eventId) throws CreateRequestException, EventNotFoundException, UserNotFoundException {
        log.info("Create request for event with id={} by user with id={}", eventId, requesterId);
        return requestService.createRequest(requesterId, eventId);
    }

    /**
     * Получить коллекцию заявок пользователя на участие в событиях.
     *
     * @param requesterId идентификатор пользователя, оставившего заявки.
     * @return коллекция заявок на участие в событиях.
     */
    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getUserRequests(@PathVariable(name = "userId") @Positive Long requesterId) {
        log.info("Get requests for user with id={}", requesterId);
        return requestService.getUserRequests(requesterId);
    }

    /**
     * Получить заявки на участие в событии, инициированным текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора события.
     * @param eventId     идентификатор события.
     * @return заявка на участие в событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestDto> getEventRequests(@PathVariable(name = "userId") @Positive Long initiatorId,
                                                   @PathVariable @Positive Long eventId) throws AccessToEventForbiddenException, EventNotFoundException {
        log.info("Get requests for event with id={} initiated by user with id={}", eventId, initiatorId);
        return requestService.getEventRequests(initiatorId, eventId);
    }

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
    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestsStatusDto updateEventRequestsStatus(@PathVariable(name = "userId") @Positive Long initiatorId,
                                                       @PathVariable @Positive Long eventId,
                                                       @RequestBody @Valid UpdateEventRequestsStatusDto updateEventRequestsStatusDto) throws AccessToEventForbiddenException, EventNotFoundException, UpdateRequestStatusException {
        log.info("Update participation request status {} for event with id={} and user with id={}", updateEventRequestsStatusDto, initiatorId, eventId);
        return requestService.updateEventRequestsStatus(initiatorId, eventId, updateEventRequestsStatusDto);
    }

    /**
     * Отменить заявку на участие пользователя в событии.
     *
     * @param requesterId идентификатор пользователя, оставившего заявку.
     * @param requestId   идентификатор заявки.
     * @return заявка на участие в событии.
     * @throws CancelRequestException   невозможно отменить заявку на участие в событии.
     * @throws RequestNotFoundException заявка с идентификатором {@code requestId} не найдена.
     */
    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable(name = "userId") @Positive Long requesterId,
                                    @PathVariable @Positive Long requestId) throws CancelRequestException, RequestNotFoundException {
        log.info("Cancel request with id={} for user with id={}", requestId, requesterId);
        return requestService.cancelUserRequest(requesterId, requestId);
    }
}
