package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateDescriptionIsNotBlank(String description) {
        if (StringUtils.isBlank(description)) {
            throw new DataValidationException("Добавьте описание!");
        }
    }

    public void validateMentorshipRequestCooldown(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .map(MentorshipRequest::getCreatedAt)
                .ifPresent(createdAt -> {
                    if (createdAt.plusMonths(MentorshipRequestService.MENTORSHIP_REQUEST_WAIT_LIMIT).isAfter(LocalDateTime.now())) {
                        throw new DataValidationException("Повторный запрос возможен только через 3 месяца.");
                    }
                });
    }

    public void validateNoAcceptedStatus(RequestStatus request) {
        if (request == RequestStatus.ACCEPTED) {
            throw new DataValidationException("Подопечный уже принят!");
        }
    }

    public void validateNotAlreadyMentor(List<User> mentees, long menteeId) {
        boolean alreadyMentee = mentees.stream().anyMatch(mentee -> mentee.getId().equals(menteeId));
        if (alreadyMentee) {
            throw new DataValidationException("Вы уже являетесь Ментором!");
        }
    }

    public void validateStatusNoReject(RequestStatus status) {
        if (status == RequestStatus.REJECTED) {
            throw new DataValidationException("Отказ уже сформирован!");
        }
    }
}