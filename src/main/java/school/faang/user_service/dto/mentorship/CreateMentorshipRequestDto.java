package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMentorshipRequestDto(
        @NotBlank(message = "Name is required")
        String description,
        @NotNull(message = "Mentor id is required")
        Long mentorId
) {
}
