package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship_request.RequestFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final UserService userService;
    private final List<RequestFilter> filters;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto request) {
        if (Objects.equals(request.requesterId(), request.receiverId())) {
            throw new IllegalArgumentException("The user cannot send a request to himself");
        }

        User requester = userService.getUserById(request.requesterId());
        User receiver = userService.getUserById(request.receiverId());

        Optional<MentorshipRequest> optionalMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(request.requesterId(), request.receiverId());

        MentorshipRequest newMentorshipRequest;
        if (optionalMentorshipRequest.isPresent()) {
            MentorshipRequest mentorshipRequest = optionalMentorshipRequest.get();
            if (LocalDateTime.now().minusMonths(3L).isBefore(mentorshipRequest.getUpdatedAt())) {
                throw new IllegalArgumentException("It's been less than three months since the last request");
            }
        }

        requester.getSentMentorshipRequests().add(mapper.toEntity(request));
        receiver.getReceivedMentorshipRequests().add(mapper.toEntity(request));
        //Будут ли изменения выше сохраняться в базе?

        newMentorshipRequest = mentorshipRequestRepository
                .create(request.requesterId(), request.receiverId(), request.description());


        return toMentorshipResponseDto(newMentorshipRequest);
    }

    public List<MentorshipResponseDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> listRequest = new ArrayList<>();
        Iterable<MentorshipRequest> iterable = mentorshipRequestRepository.findAll();
        iterable.forEach(listRequest::add);

        return getFilteredRequest(listRequest, filter).map(this::toMentorshipResponseDto).toList();
    }

    public Stream<MentorshipRequest> getFilteredRequest
            (List<MentorshipRequest> listRequest, RequestFilterDto filterDto) {
        Stream<MentorshipRequest> requestStream = listRequest.stream();
        for (RequestFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                requestStream = filter.apply(requestStream, filterDto);
            }
        }
        return requestStream;
    }

    @Transactional
    public void acceptRequest(Long id) {
        MentorshipRequest request = getMentorshipRequestById(id);

        User requester = userService.getUserById(request.getRequester().getId());
        User receiver = userService.getUserById(request.getReceiver().getId());

        if (receiver.getMentees().contains(requester)) {
            throw new IllegalArgumentException("You already have such a mentor.");
        }

        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long id, RejectionDto rejection) {
        MentorshipRequest request = getMentorshipRequestById(id);

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalArgumentException("The request has already been rejected");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
    }

    private MentorshipRequest getMentorshipRequestById(Long id) {
        return mentorshipRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("there is no such id"));
    }

    private MentorshipResponseDto toMentorshipResponseDto(MentorshipRequest request) {
        return new MentorshipResponseDto(
                request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getReceiver().getId(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
