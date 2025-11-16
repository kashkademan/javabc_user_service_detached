package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record EventStartDto(
        @NotNull(message = "Event cannot be negative")
        Long eventId,
        List<Long> attendeesIds,
        String prepareEventMessage
) {
}