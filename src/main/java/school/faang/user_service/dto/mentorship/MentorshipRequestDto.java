package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestDto(
        @NotNull(message = "ID не может быть null")
        Long id,
        @NotBlank
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status
) {
}
