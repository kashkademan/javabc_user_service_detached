package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;

public record EventRequestDto(
        @NotNull
        Long eventId,
        @NotNull
        Long userId
) {
}
