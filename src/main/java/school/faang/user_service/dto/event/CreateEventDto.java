package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

public record CreateEventDto(
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String description,
        @NotNull long ownerId,
        @NotNull @Future LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate,
        @NotNull EventType type) {
}
