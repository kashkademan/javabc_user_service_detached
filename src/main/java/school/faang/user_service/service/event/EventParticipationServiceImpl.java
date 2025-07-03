package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;

    @Override
    public void registerParticipant(long eventId, long userId) {
        log.info("Registering user {} for event {}", userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for id" + eventId));

        boolean alreadyExists = event.getAttendees().stream()
                .anyMatch(attendee -> attendee.getId() == userId);

        if (alreadyExists) {
            throw new ForbiddenException("User is already registered for this event");
        }

        eventParticipationRepository.register(eventId, userId);
        log.info("Registered event participation for eventId: {} userId: {}", eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        log.info("Unregistering user {} for event {}", userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for id" + eventId));

        boolean isRegistered = event.getAttendees().stream()
                .anyMatch(attendee -> attendee.getId() == userId);

        if (!isRegistered) {
            throw new ForbiddenException("User is not registered for this event");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }


}
