package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilter;
    private final UserContext userContext;
    private final Period mentoringRequestLimitation;

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        businessRule(requestDto.mentorId());

        MentorshipRequest createMentorshipRequest = mentorshipRequestRepository
                .create(userContext.getUserId(),
                        requestDto.mentorId(),
                        requestDto.description());

        return mentorshipRequestMapper.toMentorshipRequestDto(createMentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> mentorshipRequestAll = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter itemFilter : mentorshipRequestFilter) {
            if (itemFilter.isApplicable(filter)) {
                mentorshipRequestAll = itemFilter.apply(mentorshipRequestAll, filter);
            }
        }

        return mentorshipRequestAll
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    @Override
    public void accept(long requestId) {
        changingMentoringRequest(requestId, (mentorshipRequestResult) ->
                mentorshipRequestResult.setStatus(RequestStatus.ACCEPTED));
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        changingMentoringRequest(requestId, (mentorshipRequestResult) -> {
            mentorshipRequestResult.setStatus(RequestStatus.REJECTED);
            mentorshipRequestResult.setRejectionReason(rejectionDto.reason());
        });
    }

    private void changingMentoringRequest(long requestId, Consumer<MentorshipRequest> fnChangingMentoringRequest) {
        Optional<MentorshipRequest> mentorshipRequest = mentorshipRequestRepository.findById(requestId);

        if (mentorshipRequest.isPresent()) {
            MentorshipRequest mentorshipRequestResult = mentorshipRequest.get();

            if (mentorshipRequestResult.getStatus() != RequestStatus.PENDING) {
                generateAnError("The mentoring request with ID "
                        + requestId + " has a status other than "
                        + RequestStatus.PENDING);
            }
            fnChangingMentoringRequest.accept(mentorshipRequestResult);
            mentorshipRequestRepository.save(mentorshipRequestResult);
        } else {
            throw new EntityNotFoundException(String.format("Mentorship request %d not found", requestId));
        }
    }

    private void generateAnError(String message) {
        log.error(message);
        throw new DataValidationException(message);
    }

    private void businessRule(Long mentorId) {
        if (userContext.getUserId() == mentorId) {
            generateAnError("The user cannot send a request to himself");
        }

        Optional<MentorshipRequest> mentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(userContext.getUserId(), mentorId);

        if (mentorshipRequest.isPresent()
                && mentorshipRequest.get().getStatus() != RequestStatus.REJECTED) {

            MentorshipRequest mentorshipReq = mentorshipRequest.get();

            if (mentorshipReq.getStatus() == RequestStatus.ACCEPTED) {
                generateAnError("A mentor cannot accept a request from a user if he is already their mentor.");
            }

            LocalDateTime createdAt = mentorshipReq.getCreatedAt();
            LocalDateTime nextRequestDate = createdAt.plus(mentoringRequestLimitation);

            if (LocalDateTime.now().isBefore(nextRequestDate)) {
                long months = mentoringRequestLimitation.toTotalMonths();
                generateAnError("A request for mentoring can only be made once per " + months + " months");
            } else {
                generateAnError("The mentoring request already exists and is in the status " + RequestStatus.PENDING);
            }
        }
    }
}