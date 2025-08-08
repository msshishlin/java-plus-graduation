package ewm.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
public class PrivateEventParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getParticipationRequestsFortEvent(@PathVariable @Positive Long userId,
                                                                                 @PathVariable @Positive Long eventId) {
        log.info("Get participation requests for event with id={} and user with id={}", userId, eventId);
        return participationRequestService.getParticipationRequestsFortEvent(userId, eventId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ResultParticipationRequestStatusDto updateParticipationRequestStatus(@PathVariable @Positive Long userId,
                                                                                @PathVariable @Positive Long eventId,
                                                                                @RequestBody @Valid UpdateParticipationRequestStatusDto updateParticipationRequestStatusDto) {
        log.info("Update participation request status {} for event with id={} and user with id={}", updateParticipationRequestStatusDto, userId, eventId);
        return participationRequestService.updateParticipationRequestStatus(userId, eventId, updateParticipationRequestStatusDto);
    }
}
