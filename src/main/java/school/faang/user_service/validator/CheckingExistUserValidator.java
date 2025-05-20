package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CheckingExistUserValidator implements MentorshipValidator<MentorshipRequestDto> {

    public final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(MentorshipRequestDto dto) {
        if (!mentorshipRepository.existsById(dto.requesterId()) || !mentorshipRepository.existsById(dto.receiverId())
        ) {
            throw new ResponseStatusException(NOT_FOUND, "RequesterId or receiverId not found");
        }
    }
}