package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        EventType type,
        Long ownerId,
        EventStatus status,
        LocalDateTime createdAt
) {
}
