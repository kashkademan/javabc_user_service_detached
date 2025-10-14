package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    final EventParticipationRepository eventParticipationRepository;
    final EventRepository eventRepository;
    final UserMapper userMapper;
    final UserContext userContext;

    @Transactional
    @Override
    public void registerParticipant(long eventId, long userId) {
        if (eventRepository.findParticipatedEventsByUserId(userId).stream()
                .anyMatch(event -> event.getId() == eventId)) {
            throw new DataValidationException("User has already registered on the event");
        }
        eventParticipationRepository.register(eventId, userId);
        log.info("User {} has been registered on the event {}", userId, eventId);
    }

    @Transactional
    @Override
    public void unregisterParticipant(long eventId, long userId) {
        if (userContext.getUserId() != userId) {
            throw new ForbiddenException("You can't remove someone else's user for an event");
        }

        if (eventRepository.findParticipatedEventsByUserId(userId).stream()
                .noneMatch(event -> event.getId() == eventId)) {
            throw new DataValidationException("User hasn't registered on the event");
        }
        eventParticipationRepository.unregister(eventId, userId);
        log.info("User {} has been unregistered on the event {}", userId, eventId);
    }

    @Override
    public CountResponse countParticipantsByEventId(long eventId) {
        int participantsCount = eventParticipationRepository.countParticipants(eventId);
        log.info("Participants count for event {} is {}", eventId, participantsCount);
        return new CountResponse(participantsCount);
    }

    @Override
    public List<UserDto> getAllParticipantsByEventId(long eventId) {
        log.info("Getting participants list for event {}", eventId);
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }
}
