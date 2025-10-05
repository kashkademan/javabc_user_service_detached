package school.faang.user_service.repository.mentorship.dto;

import jakarta.validation.constraints.NotNull;

public record CreateMentorshipRequestDto(
        @NotNull(message = "Mentor ID is required")
        long mentorId,
        @NotNull(message = "Mentee ID if required")
        long menteeId
) {
}
