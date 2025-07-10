package school.faang.user_service.service.mentorship;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    public static final Period MENTORSHIP_REQUEST = Period.ofMonths(3);

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;


    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto mentorshipRequestDto) {
        long requesterId = userContext.getUserId();
        long receiverId = mentorshipRequestDto.getMentorId();

        Optional<MentorshipRequest> lastMentorship = mentorshipRequestRepository
                .findTopByRequesterIdOrderByCreatedAtDesc(requesterId);

        lastMentorship.ifPresent(request -> {
            if (request.getCreatedAt().isAfter(LocalDateTime.now().minus(MENTORSHIP_REQUEST))) {
                throw new DataValidationException("Запрос можно отправить не чаще одного раза в "
                        + MENTORSHIP_REQUEST.getMonths() + " месяца(ев)");
            }
        });

        if (requesterId == receiverId) {
            throw new DataValidationException("Нельзя отправить запрос самому себе");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(latestRequest -> {
                    if (latestRequest.getStatus() == RequestStatus.PENDING) {
                        throw new DataValidationException("Уже существует активный запрос");
                    }
                });

        MentorshipRequest request = mentorshipRequestRepository.create(
                requesterId,
                receiverId,
                mentorshipRequestDto.getDescription()
        );
        request.setRequester(
                userRepository.findById(requesterId)
                        .orElseThrow(() -> new EntityNotFoundException("менти не найден"))
        );
        request.setReceiver(
                userRepository.findById(receiverId)
                        .orElseThrow(() -> new EntityNotFoundException("Ментор не найден"))
        );

        log.info("Пользователь {} отправил запрос на менторство к {}", requesterId, receiverId);
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    private boolean isAlreadyMentor(long mentorId, long menteeId) {
        return mentorshipRequestRepository.existsByRequesterIdAndReceiverIdAndStatus(
                menteeId, mentorId, RequestStatus.ACCEPTED
        );
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filterDto) {
        if (filterDto.getRequesterId() == null && filterDto.getReceiverId() == null) {
            throw new DataValidationException("Хотя бы один из параметров:"
                    + " заказчикId или получательId должен быть задан");
        }

        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();
        Stream<MentorshipRequest> filtered = allRequests.stream();

        if (filterDto.getRequesterId() != null) {
            filtered = filtered.filter(r
                    -> filterDto.getRequesterId().equals(r.getRequester().getId()));
        }

        if (filterDto.getReceiverId() != null) {
            filtered = filtered.filter(r -> filterDto.getReceiverId().equals(r.getReceiver().getId()));
        }

        if (filterDto.getStatus() != null) {
            filtered = filtered.filter(r -> r.getStatus() == filterDto.getStatus());
        }

        return filtered
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public void accept(long requestId) {
        Long currentUserId = userContext.getUserId();

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Принять можно только запросы со статусом PENDING");
        }

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new ForbiddenException("Вы не можете принять запрос, адресованный не вам");
        }

        if (mentorshipRequestRepository.existsByReceiverIdAndRequesterIdAndStatus(
                currentUserId, request.getRequester().getId(), RequestStatus.ACCEPTED)) {
            throw new DataValidationException("Вы уже являетесь ментором для этого пользователя");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
        log.info("Пользователь {} принял запрос на менторство от {}", currentUserId, request.getRequester().getId());
    }


    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        Long currentUserId = userContext.getUserId();

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Отклонить можно только запросы со статусом PENDING");
        }

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new ForbiddenException("Вы не можете отклонить запрос, адресованный не вам");
        }

        if (rejectionDto.getReason() == null || rejectionDto.getReason().isBlank()) {
            throw new DataValidationException("Причина отказа должна быть указана");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());

        mentorshipRequestRepository.save(request);
        log.info("Пользователь {} отклонил запрос от {}. Причина: {}",
                currentUserId, request.getRequester().getId(), rejectionDto.getReason());
    }
}
