package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.Set;

public record UpdateEventDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @Nullable @FutureOrPresent
        LocalDateTime startDate,
        @Nullable @Future
        LocalDateTime endDate,
        @Nullable
        EventType type,
        @Nullable
        EventStatus status,
        Integer maxAttendees,
        Set<Long> skillsId
) {}