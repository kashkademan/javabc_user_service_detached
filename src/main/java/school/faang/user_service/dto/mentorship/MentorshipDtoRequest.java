package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MentorshipDtoRequest{
    @NotNull
    private Long mentorId;
    @NotNull
    private Long menteeId;
}
