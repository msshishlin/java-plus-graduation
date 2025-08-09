package ru.practicum.requestservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.requestservice.RequestDto;
import ru.practicum.interactionapi.dto.requestservice.RequestStatus;
import ru.practicum.interactionapi.dto.requestservice.RequestsStatusDto;
import ru.practicum.interactionapi.dto.requestservice.UpdateEventRequestsStatusDto;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.requestservice.CancelRequestException;
import ru.practicum.interactionapi.exception.requestservice.CreateRequestException;
import ru.practicum.interactionapi.exception.requestservice.RequestNotFoundException;
import ru.practicum.interactionapi.exception.requestservice.UpdateRequestStatusException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.openfeign.EventServiceClient;
import ru.practicum.interactionapi.openfeign.UserServiceClient;
import ru.practicum.requestservice.model.Request;
import ru.practicum.requestservice.repository.RequestRepository;
import ru.practicum.requestservice.service.mapper.RequestMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Сервис для работы с заявками на участие в событиях.
 */
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    /**
     * Хранилище данных о заявках на участие в событиях.
     */
    private final RequestRepository requestRepository;

    /**
     * Маппер для сущности заявки на участие в событии.
     */
    private final RequestMapper requestMapper;

    /**
     * Клиент сервиса для работы с событиями.
     */
    private final EventServiceClient eventServiceClient;

    /**
     * Клиент сервиса для работы с пользователями.
     */
    private final UserServiceClient userServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestDto createRequest(Long requesterId, Long eventId) throws CreateRequestException, EventNotFoundException, UserNotFoundException {
        if (requestRepository.findByRequesterIdAndEventId(requesterId, eventId).isPresent()) {
            throw new CreateRequestException("Нельзя создавать повторную заявку на участие в событии");
        }
        if (!userServiceClient.isUserExists(requesterId)) {
            throw new UserNotFoundException(requesterId);
        }
        if (!eventServiceClient.isEventExists(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        if (!eventServiceClient.isEventPublished(eventId)) {
            throw new CreateRequestException("Невозможно создать заявку на участие в неопубликованном событии");
        }

        EventDto event = eventServiceClient.getPublishedEventById(eventId);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new CreateRequestException("Достигнут лимит заявок на участие в событии");
        }
        if (event.getInitiator().getId().equals(requesterId)) {
            throw new CreateRequestException("Инициатору события нельзя создавать заявку на участие в своём событии");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .eventId(event.getId())
                .requesterId(requesterId)
                .status(event.getParticipantLimit() == 0 || !event.isRequestModeration() ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                .build();

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            eventServiceClient.confirmParticipation(eventId);
        }

        return requestMapper.mapToRequestDto(requestRepository.save(request));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RequestDto> getUserRequests(Long requesterId) {
        return requestMapper.mapToRequestDtoCollection(requestRepository.findByRequesterId(requesterId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RequestDto> getEventRequests(Long initiatorId, Long eventId) throws AccessToEventForbiddenException, EventNotFoundException {
        if (!eventServiceClient.isEventExists(eventId)) {
            throw new EventNotFoundException(eventId);
        }

        EventDto event = eventServiceClient.getPublishedEventById(eventId);
        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new AccessToEventForbiddenException(eventId);
        }

        return requestMapper.mapToRequestDtoCollection(requestRepository.findByEventId(eventId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestsStatusDto updateEventRequestsStatus(Long initiatorId, Long eventId, UpdateEventRequestsStatusDto updateEventRequestsStatusDto) throws AccessToEventForbiddenException, EventNotFoundException, UpdateRequestStatusException {
        if (!updateEventRequestsStatusDto.getStatus().equals(RequestStatus.CONFIRMED) && !updateEventRequestsStatusDto.getStatus().equals(RequestStatus.REJECTED)) {
            throw new UpdateRequestStatusException("Заявку на участие в событии можно перевести только в статус CONFIRMED или REJECTED");
        }

        if (!eventServiceClient.isEventExists(eventId)) {
            throw new EventNotFoundException(eventId);
        }

        EventDto event = eventServiceClient.getPublishedEventById(eventId);
        if (!event.getInitiator().getId().equals(initiatorId)) {
            throw new AccessToEventForbiddenException(eventId);
        }

        Collection<Request> requests = requestRepository.findAllById(updateEventRequestsStatusDto.getRequestIds());
        if (requests.stream().anyMatch(request -> !request.getEventId().equals(eventId))) {
            throw new UpdateRequestStatusException("Не все заявки, указанные в запросе, принадлежат данному событию");
        }

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return RequestsStatusDto.builder()
                    .confirmedRequests(requestMapper.mapToRequestDtoCollection(requests))
                    .rejectedRequests(new ArrayList<>())
                    .build();
        }

        if (updateEventRequestsStatusDto.getStatus().equals(RequestStatus.CONFIRMED) && event.getParticipantLimit() > 0 && event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new UpdateRequestStatusException("Достигнут лимит по заявкам на данное событие");
        }

        if (requests.stream().anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
            throw new UpdateRequestStatusException("Статус можно изменить только у заявок, находящихся в статусе PENDING");
        }

        int confirmedRequests = event.getConfirmedRequests();
        int participationLimit = event.getParticipantLimit();

        switch (updateEventRequestsStatusDto.getStatus()) {
            case CONFIRMED -> {
                for (Request request : requests) {
                    if (confirmedRequests < participationLimit) {
                        eventServiceClient.confirmParticipation(eventId);
                        request.setStatus(RequestStatus.CONFIRMED);
                        confirmedRequests++;
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                    }
                }
            }
            case REJECTED -> {
                for (Request participationRequest : requests) {
                    participationRequest.setStatus(RequestStatus.REJECTED);
                }
            }
        }

        requestRepository.saveAll(requests);

        return RequestsStatusDto.builder()
                .confirmedRequests(requestMapper.mapToRequestDtoCollection(requests.stream().filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED)).toList()))
                .rejectedRequests(requestMapper.mapToRequestDtoCollection(requests.stream().filter(request -> request.getStatus().equals(RequestStatus.REJECTED)).toList()))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestDto cancelUserRequest(Long requesterId, Long requestId) throws CancelRequestException, RequestNotFoundException {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        if (!request.getRequesterId().equals(requesterId)) {
            throw new CancelRequestException("Можно отменить только свой запрос на участие");
        }

        if (request.getStatus().equals(RequestStatus.CANCELED)) {
            return requestMapper.mapToRequestDto(request);
        }

        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            eventServiceClient.rejectParticipation(request.getEventId());
        }

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.mapToRequestDto(requestRepository.save(request));
    }
}
