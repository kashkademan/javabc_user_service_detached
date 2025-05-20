package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class RequesterIdNotBeEqualReceiverIdValidator implements MentorshipValidator<MentorshipRequestDto> {

    @Override
    public void validate(MentorshipRequestDto dto) {
        if (dto.requesterId() == dto.receiverId()) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester and receiver are the same person.");
        }
    }
}