package ewm.request;

import ewm.event.Event;
import ewm.event.EventRepository;
import ewm.event.EventState;
import ewm.exception.ForbiddenException;
import ewm.exception.IncorrectlyException;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.interactionapi.openfeign.UserServiceClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestSeviceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;

    /**
     * Клиент для сервиса управления пользователями.
     */
    private final UserServiceClient userServiceClient;

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestOtherEvents(Long userId) {
        UserShortDto userShortDto = userServiceClient.findUserById(userId);

        Collection<ParticipationRequest> requests = participationRequestRepository.findByRequesterId(userShortDto.getId());
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDtoCollection(requests);
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsFortEvent(Long userId, Long eventId) {
        UserShortDto userShortDto = userServiceClient.findUserById(userId);

        Event event = findEventById(eventId, userShortDto.getId());
        Collection<ParticipationRequest> requests =
                participationRequestRepository.findByEventIdAndEventInitiatorId(event.getId(), userShortDto.getId());
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDtoCollection(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        UserShortDto userShortDto = userServiceClient.findUserById(userId);

        Event event = findEventById(eventId);
        if (event.getInitiatorId().equals(userShortDto.getId())) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new ForbiddenException("Достигнут лимит запросов на участие");
        }
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requesterId(userShortDto.getId())
                .status(event.getParticipantLimit() > 0 && event.isRequestModeration()
                        ? ParticipationRequestStatus.PENDING
                        : ParticipationRequestStatus.CONFIRMED)
                .build();
        if (ParticipationRequestStatus.CONFIRMED.equals(participationRequest.getStatus())) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        UserShortDto userShortDto = userServiceClient.findUserById(userId);

        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден запрос на участие с id = %d", requestId)));
        if (!request.getRequesterId().equals(userShortDto.getId())) {
            throw new ForbiddenException("Можно отменить только свой запрос на участие");
        }
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(
                participationRequestRepository.save(request));
    }

    @Override
    @Transactional
    public ResultParticipationRequestStatusDto updateParticipationRequestStatus(Long userId, long eventId, UpdateParticipationRequestStatusDto updateParticipationRequestStatusDto) {
        UserShortDto userShortDto = userServiceClient.findUserById(userId);

        Event event = findEventById(eventId, userShortDto.getId());
        if (!ParticipationRequestStatus.CONFIRMED.equals(updateParticipationRequestStatusDto.getStatus()) &&
                !ParticipationRequestStatus.REJECTED.equals(updateParticipationRequestStatusDto.getStatus())) {
            throw new IncorrectlyException("Указан недопустимый статус. Допустимые статусы CONFIRMED и REJECTED");
        }
        Collection<ParticipationRequest> requests = participationRequestRepository.findByIdIn(updateParticipationRequestStatusDto.getRequestIds());
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return getResultParticipationRequestStatusDto(requests);
        }
        Optional<ParticipationRequest> requestWithNotValidStatus = requests.stream()
                .filter(r -> !ParticipationRequestStatus.PENDING.equals(r.getStatus()))
                .findFirst();
        if (requestWithNotValidStatus.isPresent()) {
            throw new ForbiddenException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }
        if (ParticipationRequestStatus.CONFIRMED.equals(updateParticipationRequestStatusDto.getStatus()) &&
                event.getParticipantLimit() > 0 &&
                event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит по заявкам на данное событие");
        }
        for (ParticipationRequest participationRequest : requests) {
            if (ParticipationRequestStatus.CONFIRMED.equals(updateParticipationRequestStatusDto.getStatus())) {
                if (event.getParticipantLimit() > 0 && event.getParticipantLimit() > event.getConfirmedRequests()) {
                    participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else {
                    participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
                }
            } else if (ParticipationRequestStatus.REJECTED.equals(updateParticipationRequestStatusDto.getStatus())) {
                participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
            }
        }
        eventRepository.save(event);
        participationRequestRepository.saveAll(requests);
        return getResultParticipationRequestStatusDto(requests);
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %d", eventId)));
    }

    private Event findEventById(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найдено событие с id = %d", eventId)));
        if (!event.getInitiatorId().equals(userId)) {
            throw new ForbiddenException(String.format("Доступ к событию с id = %d запрещён", event.getId()));
        }
        return event;
    }

    private ResultParticipationRequestStatusDto getResultParticipationRequestStatusDto(Collection<ParticipationRequest> participationRequests) {
        Collection<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        Collection<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (ParticipationRequest participationRequest : participationRequests) {
            if (ParticipationRequestStatus.CONFIRMED.equals(participationRequest.getStatus())) {
                confirmedRequests.add(ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(participationRequest));
            } else if (ParticipationRequestStatus.REJECTED.equals(participationRequest.getStatus())) {
                rejectedRequests.add(ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(participationRequest));
            }
        }
        return ResultParticipationRequestStatusDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
