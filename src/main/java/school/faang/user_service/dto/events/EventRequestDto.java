package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotNull;

public record EventRequestDto(
        @NotNull
        Long eventId,
        @NotNull
        Long userId
) {
}
