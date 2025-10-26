package school.faang.user_service.dto.events;

import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventResponseDto(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        EventType eventType,
        String location,
        EventStatus eventStatus,
        List<Long> skillIds
) {
}