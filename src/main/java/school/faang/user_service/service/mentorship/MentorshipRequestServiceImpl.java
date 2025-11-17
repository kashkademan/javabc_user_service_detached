package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    @Value("${mentorship.request.periodForRequest}")
    private int periodForRequest;

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {

        if (userContext.getUserId() == requestDto.mentorId()) {
            log.error("Tried to send mentorship request to self");
            throw new ForbiddenException("Forbidden to send mentorship request to self");
        }

        final Optional<MentorshipRequest> latestMentorshipRequest
                = mentorshipRequestRepository.findLatestRequest(userContext.getUserId(), requestDto.mentorId());

        if (latestMentorshipRequest.isPresent()
                && latestMentorshipRequest.get().getCreatedAt().isAfter(LocalDateTime.now()
                .minusMonths(periodForRequest))) {
            log.error("Tried to send mentorship request more than one time per three months.");
            throw new ForbiddenException("Forbidden to send mentorship request more than one time per three months");
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .create(userContext.getUserId(), requestDto.mentorId(), requestDto.description());
        log.info("Mentorship request was created by user {} to mentor {}",
                userContext.getUserId(), requestDto.mentorId());

        mentorshipOfferedEventPublisher.publish(MentorshipOfferedEvent.builder()
                .mentorshipRequestId(mentorshipRequest.getId())
                .mentorId(mentorshipRequest.getReceiver().getId())
                .menteeId(mentorshipRequest.getRequester().getId())
                .build());

        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        if (filter.requesterId() == null && filter.receiverId() == null) {
            log.error("neither the requester id nor the receiver id was specified");
            throw new DataValidationException("Receiver id and requester id cant be both null");
        }

        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(filter)) {
                mentorshipRequestStream = mentorshipRequestFilter.apply(mentorshipRequestStream, filter);
            }
        }
        return mentorshipRequestStream.map(mentorshipRequestMapper::toMentorshipRequestDto).toList();
    }

    @Override
    public void accept(long requestId) {
        final MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Membership request %d not found", requestId)));
        final User mentor = userRepository.getByIdOrThrow(userContext.getUserId());

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            log.error("User {} tried to accept not own membership request {}", userContext.getUserId(), requestId);
            throw new ForbiddenException("Forbidden to accept not own mentorship request");
        }

        if (mentor.getMentees().contains(mentorshipRequest.getRequester())) {
            log.error("User {} is already mentor of user {}",
                    userContext.getUserId(), mentorshipRequest.getRequester().getId());
            throw new DataValidationException("Cant accept mentorship request. User %d is already mentor of user %d"
                    .formatted(userContext.getUserId(), mentorshipRequest.getRequester().getId()));
        }

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            log.error("Accept failed. Status must be pending. Current status for mentorship request {} is {}",
                    requestId, mentorshipRequest.getStatus());
            throw new DataValidationException("Wrong status to accept. Must be 'Pending'. Current status is %s"
                    .formatted(mentorshipRequest.getStatus()));
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        log.info("Mentorship request {} was accepted", requestId);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        final MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Membership request %d not found", requestId)));

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            log.error("User {} tried to reject not own membership request {}", userContext.getUserId(), requestId);
            throw new ForbiddenException("Forbidden to reject not own mentorship request");
        }

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            log.error("Reject failed. Status must be pending. Current status for mentorship request {} is {}",
                    requestId, mentorshipRequest.getStatus());
            throw new DataValidationException("Wrong status to reject. Must be 'Pending'. Current status is %s"
                    .formatted(mentorshipRequest.getStatus()));
        }

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.reason());
        log.info("Mentorship request {} was rejected", requestId);
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}