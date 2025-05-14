package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventParticipationServiceImpl implements EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    @Override
    public void registerParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean isAlreadyRegistered = participants.stream()
                .anyMatch(user -> user.getId() == userId);
        if (isAlreadyRegistered) {
            throw new RuntimeException("User is already registered");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public void unregisterParticipant(long eventId, long userId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        boolean isNotRegistered = participants.stream()
                .noneMatch(user -> user.getId() == userId);
        if (isNotRegistered) {
            throw new RuntimeException("User is not registered");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Override
    public List<UserDto> getParticipants(long eventId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return users.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
