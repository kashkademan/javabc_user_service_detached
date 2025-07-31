package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.mentorship.MentorshipProperties;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.mentorshp.Mentorship;
import school.faang.user_service.entity.mentorshp.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipProperties mentorshipProperties;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        if (requestDto == null) {
            log.info("Request не найден");
            throw new IllegalArgumentException();
        }
        Long requesterId = getUserIdFromContext();
        Long receiverId = requestDto.mentorId();
        if (receiverId == null) {
            log.info("Получатель не найден");
            throw new IllegalArgumentException();
        }
        if (requesterId.equals(receiverId)) {
            log.info("Пользователь отправил запрос себе {}", requesterId);
            throw new DataValidationException("Пользователь не может отправить запрос самому себе");
        }
        Optional<User> userById = userRepository.findById(userContext.getUserId());
        if (userById.isEmpty()) {
            throw new DataValidationException("Пользователь не найден");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(lastRequest -> {
                    if (lastRequest.getCreatedAt().plusMonths(
                            mentorshipProperties.monthsToSubtract()).isAfter(LocalDateTime.now())
                    ) {
                        throw new DataValidationException(
                                "Запрос на менторство не может быть чаще, чем раз в "
                                        + mentorshipProperties.monthsToSubtract() + " месяцев."
                        );
                    }
                });
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                requesterId, receiverId, requestDto.description()
        );
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Фильтр не может быть пустым");
        }
        List<MentorshipRequest> mentorshipRequestList = (List<MentorshipRequest>) mentorshipRequestRepository
                .findMentorshipRequestsByFilters(filter.requesterId(), filter.receiverId(), filter.status());
        if (mentorshipRequestList.isEmpty()) {
            throw new DataValidationException("Не найдено запросов с такими параметрами");
        }
        return mentorshipRequestList.stream()
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    @Override
    public void accept(long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new DataValidationException("Запрос не найден")
        );
        if (!mentorshipRequest.getReceiver().getId().equals(getUserIdFromContext())) {
            throw new DataValidationException("Принять запрос на менторство может только тот, кому оно адресовано");
        }
        List<Mentorship> assignedMentorships = mentorshipRepository.findMentorshipsByMentorAndMenteeIds(
                getUserIdFromContext(), mentorshipRequest.getRequester().getId()
        );
        if (!assignedMentorships.isEmpty()) {
            throw new DataValidationException(
                    "Пользователь {} " + getUserIdFromContext() + " уже является вашим менти"
            );
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        mentorshipRepository.create(
                mentorshipRequest.getReceiver().getId(), mentorshipRequest.getRequester().getId()
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
