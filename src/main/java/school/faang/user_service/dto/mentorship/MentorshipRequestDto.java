package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record MentorshipRequestDto(
        @NotNull(message = "requesterId must not be null")
        Long requesterId,

        @NotNull(message = "receiverId must not be null")
        Long receiverId,

        @NotBlank(message = "description must not be empty")
        String description
) {
}


