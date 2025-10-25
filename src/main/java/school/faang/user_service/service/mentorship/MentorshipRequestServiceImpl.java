package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> filtered = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter requestFilter : requestFilters) {
            filtered = requestFilter.apply(filtered, filter);
        }

        return mentorshipRequestMapper.toDtoList(filtered.toList());
    }

    @Override
    public void accept(long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow();

        if (mentorshipRequest.getReceiver().getId().equals(userContext.getUserId())) {
            String message = "You can't accept your own request";
            log.warn(message);
            throw new ForbiddenException(message);
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        log.info("For user with id {} accepted request", requestId);

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow();
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.reason());
        log.info("For user with id {} rejected request", requestId);

        mentorshipRequestRepository.save(mentorshipRequest);

    }
}
