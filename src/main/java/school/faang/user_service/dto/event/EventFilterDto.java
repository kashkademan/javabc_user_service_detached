package school.faang.user_service.dto.event;

import lombok.Builder;
import school.faang.user_service.entity.event.EventType;

@Builder
public record EventFilterDto(
    String titleContains,
    String descriptionContains,
    Long ownerId,
    Long participantId,
    EventType type
) {}
