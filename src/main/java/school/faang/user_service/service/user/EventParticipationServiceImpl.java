package school.faang.user_service.service.user;

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
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;

    @Override
    public List<UserDto> getAllParticipantsByEventId(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public void registerParticipant(long eventId, long userId) {
        if (isAttendeesUser(eventId, userId) && checkUser(userId)) {
            throw new EntityNotFoundException("Вы уже зарегистрированны на событие!");
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @Override
    public CountResponse countParticipantsByEventId(long eventId) {
        return new CountResponse(eventParticipationRepository.countParticipants(eventId));
    }

    @Override
    public void unregisteredParticipation(long eventId, long userId) {
        if (!isAttendeesUser(eventId, userId) && checkUser(userId)) {
            throw new EntityNotFoundException("Вы не состоите в событии!");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    private boolean isAttendeesUser(long eventId, long userId) {
        return eventRepository.getByIdOrThrow(eventId).getAttendees().stream()
                .map(User::getId)
                .anyMatch(idUser -> idUser == userId);
    }

    private boolean checkUser(long userId) {
        if (userContext.getUserId() != userId) {
            log.warn("{} - Пытается редактировать или удалить чужие данные", userContext.getUserId());
            throw new ForbiddenException("Вы не можете редактировать или удалять чужие данные!");
        }
        return true;
    }
}
