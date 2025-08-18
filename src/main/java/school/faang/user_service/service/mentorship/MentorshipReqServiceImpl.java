package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.MentorshipRequestedEvent;
import school.faang.user_service.mapper.MentorshipReqMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipReqServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestedEventPublisher eventPublisher;
    private final MentorshipReqMapper mapper;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        User requester = getUserById(dto.getRequesterId());
        User receiver = getUserById(dto.getReceiverId());

        if (dto.getRequesterId() == dto.getReceiverId()) {
            throw new IllegalArgumentException("Нельзя запросить менторство у себя");
        }

        Optional<MentorshipRequest> latest = mentorshipRequestRepository
                .findLatestRequest(dto.getRequesterId(), dto.getReceiverId());

        if (latest.isPresent() && latest.get().getStatus() == RequestStatus.PENDING) {
            throw new IllegalArgumentException("Запрос на менторство уже отправлен");
        }

        mentorshipRequestRepository.create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());
        log.info("Создан запрос на менторство: {} → {}", dto.getRequesterId(), dto.getReceiverId());

        eventPublisher.publish(new MentorshipRequestedEvent(
                dto.getRequesterId(),
                dto.getReceiverId(),
                System.currentTimeMillis()
        ));

        return dto;
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        throw new UnsupportedOperationException("Фильтрация не реализована: нет метода выборки");
    }

    @Override
    public void acceptRequest(long id) {
        throw new UnsupportedOperationException("Нет метода findById — невозможно принять запрос");
    }

    @Override
    public void rejectRequest(long id, RejectionDto rejection) {
        throw new UnsupportedOperationException("Нет метода findById — невозможно отклонить запрос");
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));
    }
}
