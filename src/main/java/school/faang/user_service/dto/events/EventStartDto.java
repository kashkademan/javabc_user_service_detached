package school.faang.user_service.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.EventStart;

import java.util.List;

@Builder
public record EventStartDto(
        @NotNull(message = "Event cannot be negative")
        Long eventId,
        List<Long> attendeesIds,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        EventStart eventStart,
        String title
) {
}