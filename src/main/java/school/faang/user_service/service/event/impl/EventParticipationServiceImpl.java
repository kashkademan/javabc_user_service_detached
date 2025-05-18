package school.faang.user_service.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.event.EventRegisterException;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventRepository;
    private final UserContext userContext;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User registerParticipant(long eventId) {
        List<User> userRegisterEvent = getParticipant(eventId);
        if (!userRegisterEvent.contains(findUserById())) {
            eventRepository.register(eventId, findUserById().getId());
            log.info("User register");
            return findUserById();
        }
        throw new EventRegisterException("User is already register");
    }

    @Override
    @Transactional
    public void unregisterParticipant(long eventId) {
        List<User> userRegisterEvent = getParticipant(eventId);
        if (!userRegisterEvent.contains(findUserById())) {
            throw new EventRegisterException("User is not Register");
        }
        eventRepository.unregister(eventId, findUserById().getId());
        log.info("User unregister");
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getParticipant(long eventId) {
        return eventRepository.findAllParticipantsByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getParticipantsCount(long eventId) {
        return eventRepository.countParticipants(eventId);
    }

    private User findUserById() {
        long userId = userContext.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User by id %d not Found", userId)));
    }
}
