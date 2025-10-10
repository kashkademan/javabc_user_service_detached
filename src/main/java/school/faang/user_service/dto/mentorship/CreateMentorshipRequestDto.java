package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;

public record CreateMentorshipRequestDto(
        @NotNull String description,
        @NotNull Long mentorId
) {
}