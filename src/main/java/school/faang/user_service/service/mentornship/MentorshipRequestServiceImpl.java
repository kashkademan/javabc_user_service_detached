package school.faang.user_service.service.mentornship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> filters;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();
        UserDto requester = userService.findUserById(requesterId);
        UserDto receiver = userService.findUserById(receiverId);

        if (requester.equals(receiver)) {
            throw new IllegalArgumentException("User with id %d could not send mentorship request to himself"
                    .formatted(requesterId));
        }

        Optional<MentorshipRequest> latestMentorshipRequestOpt = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                        .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3)));

        if (latestMentorshipRequestOpt.isPresent()) {
            throw new IllegalArgumentException("User %d can only create request once in 3 month".formatted(requesterId));
        }
        mentorshipRequestDto.setRequestStatus(RequestStatus.PENDING);
        MentorshipRequest request = mentorshipRequestRepository
                .save(mentorshipRequestMapper.toMentorshipRequestEntity(mentorshipRequestDto));
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        Stream<MentorshipRequest> requestStream =
                StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false);
        filters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(filter))
                .forEach(requestFilter -> requestFilter.apply(requestStream, filter));
        return mentorshipRequestMapper.toMentorshipRequestDtoList(requestStream.toList());
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long id) {
        MentorshipRequestDto foundDto = findMentorshipRequestById(id);
        UserDto requester = userService.findUserById(foundDto.getRequesterId());
        UserDto receiver = userService.findUserById(foundDto.getReceiverId());
        if (requester.getMentors().contains(receiver)) {
            throw new IllegalArgumentException("User with id %d is already mentor for user with id %d"
                    .formatted(receiver.getId(), requester.getId()));
        }
        requester.getMentors().add(receiver);
        userService.updateUser(requester);
        foundDto.setRequestStatus(RequestStatus.ACCEPTED);
        MentorshipRequest request = mentorshipRequestRepository
                .save(mentorshipRequestMapper.toMentorshipRequestEntity(foundDto));
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    @Override
    public MentorshipRequestDto rejectRequest(Long id, RejectionDto rejectionDto) {
        MentorshipRequestDto foundDto = findMentorshipRequestById(id);
        foundDto.setRequestStatus(RequestStatus.REJECTED);
        foundDto.setRejectionReason(rejectionDto.getReason());
        MentorshipRequest request = mentorshipRequestRepository
                .save(mentorshipRequestMapper.toMentorshipRequestEntity(foundDto));
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    private MentorshipRequestDto findMentorshipRequestById(Long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship with id %d not found".formatted(id)));
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }
}