package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserDto;

public record CreateMentorshipRequestDto(
        @NotBlank(message = "Описание не может быть пустым")
        String description,
        @NotNull(message = "MentorId не может быть null")
        Long mentorId,
        UserDto requester,
        UserDto receiver
) {
}
