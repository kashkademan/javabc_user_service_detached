package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.Set;


public record CreateEventDto(
        @NotBlank
        String title,
        @NotBlank @Size(min = 1, max = 64)
        String description,
        @Nullable @FutureOrPresent
        LocalDateTime startDate,
        @Nullable @Future
        LocalDateTime endDate,
        EventType type,
        Set<Long> skillsId
) {}