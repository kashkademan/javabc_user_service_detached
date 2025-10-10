package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UpdateEventDto(
        @Nullable
        String title,
        @Nullable
        String description,
        @Nullable @FutureOrPresent
        LocalDateTime startDate,
        @Nullable @Future
        LocalDateTime endDate,
        @Nullable
        EventType type,
        @Nullable
        EventStatus status,
        @Nullable
        Integer maxAttendees,
        @Nullable
        Set<Long> skillsId
) {}