package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        try {
            eventParticipationRepository.register(eventId, userId);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("The user is already registered for this event.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user for event.", e);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        try {
            eventParticipationRepository.unregister(eventId, userId);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("The user is not registered for this event.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user for event.", e);
        }
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}