package school.faang.user_service.dto.event;

import jakarta.validation.constraints.AssertTrue;
import school.faang.user_service.entity.event.EventType;

public record EventFilterDto(
        String titleContains,
        String descriptionContains,
        Long ownerId,
        Long participantId,
        EventType type
) {
    @AssertTrue(message = "At least one filter criteria must be provided")
    public boolean isAtLeastOneFilterPresent() {
        return titleContains != null && !titleContains.trim().isEmpty()
                || descriptionContains != null && !descriptionContains.trim().isEmpty()
                || ownerId != null
                || participantId != null
                || type != null;
    }
}
