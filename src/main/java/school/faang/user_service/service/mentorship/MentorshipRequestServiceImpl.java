package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
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
        for (MentorshipRequestFilter requestFilter : filters) {
            if (requestFilter.isApplicable(filter)) {
                requestStream = requestFilter.apply(requestStream, filter);
            }
        }
        return mentorshipRequestMapper.toMentorshipRequestDtoList(requestStream.toList());
    }

    @Transactional
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

    @Transactional
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
        return mentorshipRequestRepository.findById(id)
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship with id %d not found".formatted(id)));
    }
}