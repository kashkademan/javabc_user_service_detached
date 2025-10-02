package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDto(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate,

        @NotNull
        EventType type,

        @NotBlank
        String location,

        @NotNull
        List<Long> skillIds
) {
}
