package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    public void registerParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean alreadyRegistered = participants.stream()
                .allMatch(user -> user.getId() == userId);

        if (alreadyRegistered) {
            throw new IllegalArgumentException("User is already registered for the event");
        }

        eventParticipationRepository.register(eventId, userId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean alreadyRegistered = participants.stream()
                .allMatch(user -> user.getId() == userId);

        if (!alreadyRegistered) {
            throw new IllegalArgumentException("User is already unregistered from the event");
        }

        eventParticipationRepository.unregister(eventId, userId);
    }

    public List<UserDto> getParticipants(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
