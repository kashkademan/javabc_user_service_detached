package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record EventUpdateDto(
        @Size(min = 1, max = 255)
        String title,
        String description,
        @FutureOrPresent
        LocalDateTime startDate,
        @Future
        LocalDateTime endDate,
        EventType type,
        EventStatus status,
        Integer maxAttendees,
        Set<Long> skillsId
) {}