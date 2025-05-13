package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filters.mentorship_request.RequestFilter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final UserRepository userRepo;
    private final List<RequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto request) {
        if (Objects.equals(request.getRequesterId(), request.getReceiverId())) {
            throw new IllegalArgumentException("The user cannot send a request to himself");
        }

        User requester = userRepo.findById(request.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException
                        ("The Requester with id =" + request.getRequesterId() + " does not exist"));

        User receiver = userRepo.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException
                        ("The Receiver with id =" + request.getReceiverId() + " does not exist"));

        Optional<MentorshipRequest> optionalMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(request.getRequesterId(), request.getReceiverId());
        if (optionalMentorshipRequest.isPresent()) {
            MentorshipRequest mentorshipRequest = optionalMentorshipRequest.get();
            if (ChronoUnit.MONTHS.between(mentorshipRequest.getUpdatedAt(), LocalDate.now()) > 3) {
                throw new IllegalArgumentException("It's been less than three months since the last request");
            }
            requester.getSentMentorshipRequests().add(mapper.toEntity(request));
            receiver.getReceivedMentorshipRequests().add(mapper.toEntity(request));

            mentorshipRequestRepository
                    .create(request.getRequesterId(), request.getReceiverId(), request.getDescription());

        } else {
            mentorshipRequestRepository
                    .create(request.getRequesterId(), request.getReceiverId(), request.getDescription());
        }
        return request;
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> listRequest = new ArrayList<>();
        Iterable<MentorshipRequest> iterable = mentorshipRequestRepository.findAll();
        iterable.forEach(listRequest::add);

        return getFilteredRequest(listRequest, filter).map(mapper::toDto).toList();
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
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("there is no such id"));

        User requester = userRepo.findById(request.getRequester().getId())
                .orElseThrow(() -> new IllegalArgumentException
                        ("The Requester with id =" + request.getRequester().getId() + " does not exist"));

        User receiver = userRepo.findById(request.getReceiver().getId())
                .orElseThrow(() -> new IllegalArgumentException
                        ("The Receiver with id =" + request.getReceiver().getId() + " does not exist"));

        if (!receiver.getMentees().contains(requester)) {
            receiver.getMentees().add(requester);
            requester.getMentors().add(receiver);

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new IllegalArgumentException("The request has already been rejected");
            }

            request.setStatus(RequestStatus.ACCEPTED);
            mentorshipRequestRepository.save(request);
        } else {
            throw new IllegalArgumentException("You already have such a mentor.");
        }
    }

    @Transactional
    public void rejectRequest(Long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("there is no such id"));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalArgumentException("The request has already been rejected");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
    }
}
