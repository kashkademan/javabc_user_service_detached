package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;

public record CreateMentorshipDto(
        @NotNull(message = "Mentor ID is required")
        Long mentorId,
        @NotNull(message = "Mentee ID if required")
        Long menteeId
) {
}
