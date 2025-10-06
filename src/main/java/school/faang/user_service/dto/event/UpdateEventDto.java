package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.Set;

public record UpdateEventDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @FutureOrPresent LocalDateTime startDate,
        @NotNull @Future LocalDateTime endDate,
        @NotNull EventType type,
        @NotNull EventStatus status,
        Integer maxAttendees,
        Set<Long> skillsId
) {}
