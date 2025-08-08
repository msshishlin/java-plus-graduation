package ewm.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PrivateParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getParticipationRequestOtherEvents(@PathVariable @Positive Long userId) {
        log.info("Get participation request for user with id={}", userId);
        return participationRequestService.getParticipationRequestOtherEvents(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable @Positive Long userId,
                                                              @RequestParam @Positive Long eventId) {
        log.info("Create participation request for event with id={} and user with id={}", eventId, userId);
        return participationRequestService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable @Positive Long userId,
                                                              @PathVariable @Positive Long requestId) {
        log.info("Cancel participation request with id={} for user with id={}", requestId, userId);
        return participationRequestService.cancelParticipationRequest(userId, requestId);
    }
}
