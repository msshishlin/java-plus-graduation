package ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findByRequesterId(Long requesterId);

    Collection<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    Collection<ParticipationRequest> findByIdIn(Collection<Long> ids);
}
