package school.faang.user_service.dto.event;

import jakarta.annotation.Nullable;
import lombok.Builder;
import school.faang.user_service.entity.event.EventType;

@Builder
public record EventFilterDto(
        @Nullable
        String titleContains,
        @Nullable
        String descriptionContains,
        @Nullable
        Long ownerId,
        @Nullable
        Long participantId,
        @Nullable
        EventType type
) {}
