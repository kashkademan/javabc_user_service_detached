package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreateDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestViewDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.mentorship.MentorshipRequestFilterService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;

    private final MentorshipRepository mentorshipRepository;

    @Qualifier("mentorshipRequestMapper")
    private final MentorshipRequestMapper mapper;

    private final UserContext userContext;

    private final MentorshipRequestFilterService filterService;

    @Override
    @Transactional
    public MentorshipRequestViewDto create(MentorshipRequestCreateDto createDto) {
        Long requesterId = userContext.getUserId();
        Long receiverId = createDto.receiverId();

        if (requesterId.equals(receiverId)) {
            throw new BadRequestException("You cannot request mentorship from your self");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(request -> {
                    if (request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
                        throw new BadRequestException("You can only send mentorship request once every 3 months");
                    }
                });

        MentorshipRequest request = mentorshipRequestRepository
                .create(requesterId, receiverId, createDto.description());
        return mapper.toEntity(request);
    }

    @Override
    @Transactional
    public List<MentorshipRequestViewDto> getByFilters(MentorshipRequestFilterDto filter) {
        if (filter.requesterId() == null && filter.receiverId() == null) {
            throw new BadRequestException("RequesterId or ReceiverId must be provided");
        }

        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();
        List<MentorshipRequest> filtered = filterService.getFilteredList(allRequests, filter);

        return filtered.stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    @Transactional
    public void accept(long requestId) {
        Long currentUserId = userContext.getUserId();

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new BadRequestException("Mentorship request not found"));

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new BadRequestException("Only the receiver can accept the mentorship request");
        }

        boolean exists = mentorshipRepository.existsByMentorIdAndMenteeId(request.getReceiver().getId(),
                request.getRequester().getId());
        if (exists) {
            throw new BadRequestException("Mentorship relationship already exists");
        }

        mentorshipRepository.createMentorship(request.getReceiver().getId(), request.getRequester().getId());

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void reject(long requestId, RejectionDto rejectionDto) {
        Long currentUserId = userContext.getUserId();

        if (rejectionDto.reason() == null || rejectionDto.reason().isBlank()) {
            throw new BadRequestException("Rejection reason must not be empty");
        }

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new BadRequestException("Mentorship request not found"));

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new BadRequestException("Only the receiver can reject the mentorship request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());
        mentorshipRequestRepository.save(request);
    }
}