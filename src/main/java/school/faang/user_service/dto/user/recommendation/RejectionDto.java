package school.faang.user_service.dto.user.recommendation;

import jakarta.validation.constraints.NotBlank;

public record RejectionDto(
        @NotBlank(message = "Reason must not be blank")
        String reason) {
}