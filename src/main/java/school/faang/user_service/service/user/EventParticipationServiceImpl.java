package school.faang.user_service.service.user;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;

    @Override
    public List<UserDto> getAllParticipantsByEventId(Long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void registerParticipant(Long eventId, Long userId) {
       if (isAttendeesUser(eventId, userId) && checkUser(userId)) {
            throw new EntityNotFoundException("Вы уже зарегистрированны на событие!");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public CountResponse countParticipantsByEventId(Long eventId) {
        return new CountResponse(eventParticipationRepository.countParticipants(eventId));
    }

    @Override
    public void unregisteredParticipation(Long eventId, Long userId) {
        if (!isAttendeesUser(eventId, userId) && checkUser(userId)) {
            throw new EntityNotFoundException("Вы не состоите в событии!");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    private boolean isAttendeesUser(Long eventId, Long userId) {
        return eventRepository.getByIdOrThrow(eventId).getAttendees().stream()
                .map(User::getId)
                .anyMatch(idUser -> idUser.equals(userId));
    }

    private boolean checkUser(Long userId) {
        if (userContext.getUserId() != userId) {
            log.warn("{} - Пытается редактировать или удалить чужие данные", userContext.getUserId());
            throw new ForbiddenException("Вы не можете редактировать или удалять чужие данные!");
        }
        return true;
    }
}
