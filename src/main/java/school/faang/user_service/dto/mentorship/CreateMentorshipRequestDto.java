package school.faang.user_service.dto.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateMentorshipRequestDto {
    private final String description;
    private final Long mentorId;
}
