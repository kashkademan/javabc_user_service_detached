package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EventRequestDto(
        @NotNull(message = "User cannot be empty")
        @Positive(message = "User cannot be negative")
        Long userId
) {
}
