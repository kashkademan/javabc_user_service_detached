package school.faang.user_service.service.mentorship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.mentorshp.Mentorship;
import school.faang.user_service.entity.mentorshp.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final int monthsToSubtract;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;

    public MentorshipRequestServiceImpl(
            @Value("${user-service.months-to-subtract}")
            int mothsToSubtract, MentorshipRequestRepository mentorshipRequestRepository, UserRepository userRepository,
            MentorshipRequestMapper mentorshipRequestMapper,
            UserContext userContext, MentorshipRepository mentorshipRepository) {
        this.monthsToSubtract = mothsToSubtract;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.userRepository = userRepository;
        this.mentorshipRequestMapper = mentorshipRequestMapper;
        this.userContext = userContext;
        this.mentorshipRepository = mentorshipRepository;
    }

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        if (requestDto == null) {
            log.info("Request не найден");
            throw new IllegalArgumentException();
        }
        UserDto requester = requestDto.requester();
        UserDto receiver = requestDto.receiver();
        if (requester == null || receiver == null) {
            log.info("Отправитель или получатель не найден {} {}", requester, receiver);
            throw new IllegalArgumentException();
        }
        Long userIdFromContext = getUserIdFromContext();
        if (userIdFromContext.equals(receiver.id())) {
            log.info("Пользователь отправил запрос себе {}", userIdFromContext);
            throw new DataValidationException("Пользователь не может отправить запрос самому себе");
        }
        Optional<User> userById = userRepository.findById(userContext.getUserId());
        if (userById.isEmpty()) {
            throw new DataValidationException("Пользователь не найден");
        }

        mentorshipRequestRepository.findLatestRequest(requester.id(), receiver.id())
                .ifPresent(lastRequest -> {
                    if (lastRequest.getCreatedAt().plusMonths(monthsToSubtract).isAfter(LocalDateTime.now())) {
                        lastRequest.setStatus(RequestStatus.REJECTED);
                        throw new DataValidationException(
                                "Запрос на менторство не может быть чаще, чем раз в "
                                        + monthsToSubtract + " месяцев."
                        );
                    }
                });
        mentorshipRepository.create(requester.id(), receiver.id());
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                requester.id(), receiver.id(), requestDto.description()
        );
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    public MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest) {
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequestList = (List<MentorshipRequest>) mentorshipRequestRepository.findAll();
        return mentorshipRequestList.stream()
                .filter(mentorshipRequest ->
                        mentorshipRequest.getRequester().getId().equals(filter.requesterId()))
                .filter(mentorshipRequest ->
                        mentorshipRequest.getReceiver().getId().equals(filter.receiverId()))
                .filter(mentorshipRequest ->
                        mentorshipRequest.getStatus().equals(filter.status()))
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    @Override
    public void accept(long requestId) {
        Optional<MentorshipRequest> mentorshipRequestById = mentorshipRequestRepository.findById(requestId);
        List<Mentorship> mentorshipsByMentorId = mentorshipRepository.findMentorshipsByMentorId(getUserIdFromContext());
        if (mentorshipRequestById.isEmpty()) {
            throw new DataValidationException("Запрос не найден");
        }
        if (!mentorshipRequestById.get().getReceiver().getId().equals(getUserIdFromContext())) {
            throw new DataValidationException("Принять запрос на менторство может только тот, кому оно адресовано");
        }
        for (Mentorship mentorship : mentorshipsByMentorId) {
            if (mentorship.getMentorId().getId().equals(getUserIdFromContext())) {
                throw new DataValidationException(
                        "Пользователь {} " + getUserIdFromContext() + " уже является вашим менти"
                );
            }
        }
        mentorshipRequestById.get().setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequestById.get());
        mentorshipRepository.create(
                mentorshipRequestById.get().getRequester().getId(), mentorshipRequestById.get().getReceiver().getId()
        );
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        Optional<MentorshipRequest> mentorshipRequestById = mentorshipRequestRepository.findById(requestId);
        if (mentorshipRequestById.isEmpty()) {
            throw new DataValidationException("Запрос не найден");
        }
        if (!mentorshipRequestById.get().getReceiver().getId().equals(getUserIdFromContext())) {
            throw new DataValidationException("Отклонить запрос на менторство может только тот, кому оно адресовано");
        }
        mentorshipRequestById.get().setStatus(RequestStatus.REJECTED);
        mentorshipRequestById.get().setRejectionReason(rejectionDto.reason());
        mentorshipRequestRepository.save(mentorshipRequestById.get());
    }

    private Long getUserIdFromContext() {
        return userContext.getUserId();
    }

}
