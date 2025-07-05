package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    @Override
    public void registerParticipant(long eventId, long userId) {
        log.info("Registering user {} for event {}", userId, eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found for id: " + eventId);
        }

        if (eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ForbiddenException("User is already registered for this event");
        }

        eventParticipationRepository.register(eventId, userId);
        log.info("Registered event participation for eventId: {} userId: {}", eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        log.info("Unregistering user {} for event {}", userId, eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found for id: " + eventId);
        }

        if (!eventParticipationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new ForbiddenException("User is already unregistered for this event");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    @Override
    public CountResponse countParticipantsByEventId(long eventId) {
        log.info("Counting participants for event {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found for id: " + eventId);
        }
        int count = eventParticipationRepository.countParticipants(eventId);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getAllParticipantsByEventId(long eventId) {
        log.info("Getting all participants for event {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found for id: " + eventId);
        }

        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }
}
