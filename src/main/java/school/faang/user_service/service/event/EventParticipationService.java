package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    private boolean isUserRegistered(List<User> participants, long userId) {
        return participants.stream()
                .anyMatch(user -> user.getId() == userId);
    }

    @Transactional
    public void registerParticipant(long eventId, long userId) {

        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }

        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (isUserRegistered(participants, userId)) {
            throw new IllegalArgumentException("User is already registered for the event");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {

        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }

        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        if (!isUserRegistered(participants, userId)) {
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
