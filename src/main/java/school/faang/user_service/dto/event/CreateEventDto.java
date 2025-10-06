package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

public record CreateEventDto(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Description must not be blank")
        String description,

        @NotNull(message = "Start date must not be null")
        LocalDateTime startDate,

        @NotNull(message = "End date must not be null")
        LocalDateTime endDate,

        @NotNull(message = "Event type must not be null")
        EventType type
) {
}
