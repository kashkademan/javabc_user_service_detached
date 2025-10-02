package school.faang.user_service.dto.event;

import school.faang.user_service.entity.event.EventType;

import java.util.List;

public record EventFilterDto(
        String titleContains,
        String descriptionContains,
        Long ownerId,
        Long participantId,
        EventType type,
        List<Long> skillIds
) {
}
