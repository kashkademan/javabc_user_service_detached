package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.validator.Validator;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CheckingExistUserValidator implements Validator<MentorshipRequestDto> {

    public final MentorshipRepository mentorshipRepository;

    @Override
    public void validate(MentorshipRequestDto dto) {
        if (!mentorshipRepository.existsById(dto.requesterId()) || !mentorshipRepository.existsById(dto.receiverId())
        ) {
            throw new ResponseStatusException(NOT_FOUND, "RequesterId or receiverId not found");
        }
    }
}