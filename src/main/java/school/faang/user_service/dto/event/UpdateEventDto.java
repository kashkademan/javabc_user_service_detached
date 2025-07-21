package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Builder
public record UpdateEventDto(
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String description,
        @NotNull Long ownerId,
        @NotNull @Future LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate,
        @NotNull EventType type,
        @NotNull EventStatus status
) {
}
