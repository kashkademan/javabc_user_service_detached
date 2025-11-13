package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record EventStartDto(
        @NotNull(message = "Event cannot be negative")
        Long eventId,
        List<@NotNull(message = "Participants cannot be null")
        @Positive(message = "Participants cannot be negative")
                Long> participantsIds
) {
}