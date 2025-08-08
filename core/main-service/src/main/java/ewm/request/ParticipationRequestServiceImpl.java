package ewm.request;

import ewm.exception.ForbiddenException;
import ewm.exception.IncorrectlyException;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.EventState;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.openfeign.EventServiceClient;
import ru.practicum.interactionapi.openfeign.UserServiceClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;

    /**
     * Клиент сервиса для работы с событиями.
     */
    private final EventServiceClient eventServiceClient;

    /**
     * Клиент сервиса для работы с пользователями.
     */
    private final UserServiceClient userServiceClient;

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestOtherEvents(Long userId) {
        UserDto userShortDto = userServiceClient.findUserById(userId);

        Collection<ParticipationRequest> requests = participationRequestRepository.findByRequesterId(userShortDto.getId());
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDtoCollection(requests);
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsFortEvent(Long userId, Long eventId) {
        UserDto userShortDto = userServiceClient.findUserById(userId);

        Collection<ParticipationRequest> requests = participationRequestRepository.findByEventIdAndRequesterId(eventId, userShortDto.getId());
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDtoCollection(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        UserDto userShortDto = userServiceClient.findUserById(userId);

        EventDto event = eventServiceClient.getPublishedEventById(eventId);
        if (event.getInitiator().getId().equals(userShortDto.getId())) {
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
                .eventId(event.getId())
                .requesterId(userShortDto.getId())
                .status(event.getParticipantLimit() > 0 && event.isRequestModeration()
                        ? ParticipationRequestStatus.PENDING
                        : ParticipationRequestStatus.CONFIRMED)
                .build();

        if (ParticipationRequestStatus.CONFIRMED.equals(participationRequest.getStatus())) {
            eventServiceClient.confirmParticipation(eventId);
        }

        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        UserDto userShortDto = userServiceClient.findUserById(userId);

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
        EventDto event = eventServiceClient.getPublishedEventById(eventId);
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
                    eventServiceClient.confirmParticipation(eventId);
                } else {
                    participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
                }
            } else if (ParticipationRequestStatus.REJECTED.equals(updateParticipationRequestStatusDto.getStatus())) {
                participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
            }
        }

        participationRequestRepository.saveAll(requests);
        return getResultParticipationRequestStatusDto(requests);
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
