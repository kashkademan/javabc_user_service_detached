package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateEventDto(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 64, message = "Title increased the number of characters")
        String title,
        @NotBlank(message = "Location cannot be empty")
        @Size(max = 4096, message = "Description increased the number of characters")
        String description,
        @NotNull(message = "Specify start date for event!")
        LocalDateTime startDate,
        @NotNull(message = "Specify end date for event!")
        LocalDateTime endDate,
        @NotNull(message = "Specify maximum attendees for event!")
        @Positive(message = "Attendees cannot be negative")
        Integer maxAttendees,
        @NotNull(message = "Specify type for event!")
        EventType eventType,

        @NotBlank(message = "Specify related skills")
        List<@NotNull(message = "Related skills cannot be empty") Long> relatedSkillsId
) {
}