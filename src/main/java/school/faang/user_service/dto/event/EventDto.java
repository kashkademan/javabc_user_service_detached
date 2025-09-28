package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record EventDto(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        EventType type,
        Long ownerId,
        List<Long> participantIds,
        EventStatus status,
        LocalDateTime createdAt
) {
}
