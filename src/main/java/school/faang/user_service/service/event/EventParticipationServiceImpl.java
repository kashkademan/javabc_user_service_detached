package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.EventParticipationDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
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
    public EventParticipationDto registerParticipant(long eventId, long userId) {
        log.info("User registration {} for event {}", userId, eventId);
        List<User> attendees = eventRepository.getByIdOrThrow(eventId).getAttendees();
        attendees.stream().filter(user -> user.getId() == userId).findFirst().ifPresent(user -> {
            log.warn("Participant {} try to make double registration for event {}", userId, eventId);
            throw new ForbiddenException("Double registration is forbidden!");
        });
        log.info("User {} successfully registered for event {}", userId, eventId);
        return eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        log.info("Unregistering user {} from event {}", userId, eventId);
        List<User> attendees = eventRepository.getByIdOrThrow(eventId).getAttendees();
        if (attendees.stream().anyMatch(user -> user.getId() == userId)) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("User {} successfully unregistered from event {}", userId, eventId);
        } else {
            log.warn("User {} was not registered for event {}, nothing to unregister", userId, eventId);
        }
    }

    @Override
    public CountResponse countParticipantsByEventId(long eventId) {
        log.debug("Counting all participants for event {}", eventId);
        return new CountResponse(eventParticipationRepository.countParticipants(eventId));
    }

    @Override
    public List<UserDto> getAllParticipantsByEventId(long eventId) {
        log.info("Getting all participants for event {}", eventId);
        List<User> allParticipantsByEventId = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        log.debug("Event {} participants count = {}", eventId, allParticipantsByEventId.size());
        return allParticipantsByEventId.stream().map(userMapper::toUserDto).toList();
    }
}
