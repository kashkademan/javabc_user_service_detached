package school.faang.user_service.dto.event;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record CreateEventDto(
        @NotBlank
        String title,
        @Size(min = 1, max = 255)
        String description,
        @NotNull @FutureOrPresent
        LocalDateTime startDate,
        @NotNull @Future
        LocalDateTime endDate,
        @NotNull
        EventType type,
        Set<Long> skillsId
) {}