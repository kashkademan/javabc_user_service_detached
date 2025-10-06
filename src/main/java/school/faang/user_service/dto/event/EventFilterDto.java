package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventType;

public record EventFilterDto(
        @NotBlank(message = "Title must not be blank")
        String titleContains,

        @NotBlank(message = "Description must not be blank")
        String descriptionContains,

        @NotNull(message = "Owner id must not be null")
        Long ownerId,

        @NotNull(message = "Participant id must not be null")
        Long participantId,

        @NotNull(message = "Event type must not be null")
        EventType type
) {
}
