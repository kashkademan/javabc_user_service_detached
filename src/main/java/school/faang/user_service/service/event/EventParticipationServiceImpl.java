package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        if (isParticipantRegistered(eventId, userId)) {
            throw new DataValidationException("User %d already registered for event %d".formatted(userId, eventId));
        } else {
            eventParticipationRepository.register(eventId, userId);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (isParticipantRegistered(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new DataValidationException("User %d not registered for event %d".formatted(userId, eventId));
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public long getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isParticipantRegistered(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}