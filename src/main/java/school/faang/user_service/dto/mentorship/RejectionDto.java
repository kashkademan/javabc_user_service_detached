package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;

public record RejectionDto(
        @NotBlank(message = "Rejection reason must not be empty")
        String reason
) {}