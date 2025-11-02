package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.event.EventType;

@Builder
public record AllEventByFilterDto(

        @NotBlank(message = "Title cannot be empty")
        @Size(max = 64, message = "Title increased the number of characters")
        String titleContains,

        @NotBlank(message = "Location cannot be empty")
        @Size(max = 4096, message = "Description increased the number of characters")
        String descriptionContains,

        @Positive(message = "Owner cannot be negative")
        Long ownerId,

        @Positive(message = "Count attendees cannot be negative")
        Long participantId,

        @NotNull(message = "Specify type for event!")
        EventType type
) {
}