package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserContext userContext;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> requestFilters;

    @Setter
    @Value("${mentorship.months.limit}")
    private int monthsLimit;

    @Override
    @Transactional
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        long userId = userContext.getUserId();
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository
                .findLatestRequest(userId, requestDto.mentorId());
        if (latestRequest.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime createdAt = latestRequest.get().getCreatedAt();

            if (createdAt.isAfter(now.minusMonths(monthsLimit))) {
                String message = String.format("You can't create new request until %s",
                        createdAt.plusMonths(monthsLimit));
                log.warn(message);
                throw new ForbiddenException(message);
            }
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .create(userId, requestDto.mentorId(), requestDto.description());
        log.info("For user with id {} created request", userId);

        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> filtered = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter requestFilter : requestFilters) {
            filtered = requestFilter.apply(filtered, filter);
        }

        return mentorshipRequestMapper.toDtoList(filtered.toList());
    }

    @Override
    @Transactional
    public MentorshipRequestDto accept(long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow();

        if (mentorshipRequest.getReceiver().getId().equals(userContext.getUserId())) {
            String message = String.format("self-mentorship is forbidden: requestId=%d", requestId);
            log.warn(message);
            throw new ForbiddenException(message);
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        log.info("For user with id {} accepted request", requestId);

        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    @Override
    @Transactional
    public MentorshipRequestDto reject(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow();
        long userId = userContext.getUserId();
        if (requestId != userId) {
            String message = String.format("You can't reject request with id %d", requestId);
            log.warn(message);
            throw new ForbiddenException(message);
        }
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.reason());
        log.info("For user with id {} rejected request", requestId);

        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestRepository.save(mentorshipRequest));
    }
}
