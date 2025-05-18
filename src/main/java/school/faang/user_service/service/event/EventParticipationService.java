package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RegisterParticipantRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper registerParticipantRequestMapper;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        try {
            eventParticipationRepository.register(eventId, userId);
        } catch (Exception e) {
            log.error("Error registering user {} for event {}: {}", userId, eventId, e.getMessage(), e);
            throw new RuntimeException("Error registering user for event.", e);
        }
    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        try {
            eventParticipationRepository.unregister(eventId, userId);
        } catch (Exception e) {
            log.error("Error unregistering user {} from event {}: {}", userId, eventId, e.getMessage(), e);
            throw new RuntimeException("Error unregistering user from event.", e);
        }
    }

    public List<RegisterParticipantRequestDto> getParticipant(long eventId) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return registerParticipantRequestMapper.toRegisterParticipantRequestDtoList(participants);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}