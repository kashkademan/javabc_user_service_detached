package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.Validator;

import java.time.LocalDateTime;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@RequiredArgsConstructor
public class CheckingRequestDateValidator implements Validator<MentorshipRequestDto> {
    private static final long TIME_FOR_REQUEST = 3;
    public final MentorshipRepository mentorshipRepository;
    public final MentorshipRequestRepository mentorshipRequestRepository;

    @Override
    public void validate(MentorshipRequestDto dto) {
        mentorshipRequestRepository.findLatestRequest(dto.requesterId(), dto.receiverId())
                .map(MentorshipRequest::getCreatedAt)
                .ifPresent(createdAt -> {
                    if (createdAt.isAfter(LocalDateTime.now().minusMonths(TIME_FOR_REQUEST))) {
                        throw new ResponseStatusException(BAD_REQUEST,
                                "You have already made a request during this period");
                    }
                });
    }
}