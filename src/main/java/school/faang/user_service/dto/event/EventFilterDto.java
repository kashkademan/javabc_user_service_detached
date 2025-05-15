package school.faang.user_service.dto.event;

import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@Builder
public record EventFilterDto(
        String title,
        Long ownerId,
        String location,
        EventType eventType,
        EventStatus eventStatus
) {
}
