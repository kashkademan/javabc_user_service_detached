package school.faang.user_service.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.service.events.EventService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UpdateEventDto(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 64, message = "Title increased the number of characters")
        String title,

        @NotBlank(message = "Location cannot be empty")
        @Size(max = 4096, message = "Description increased the number of characters")
        String description,

        @NotNull(message = "Specify start date for event!")
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime startDate,

        @NotNull(message = "Specify end date for event!")
        @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime endDate,

        @NotNull(message = "Specify maximum attendees for event!")
        @Positive(message = "Attendees cannot be negative")
        Integer maxAttendees,

        @NotNull(message = "Specify event status for event!")
        EventStatus eventStatus,

        @NotEmpty(message = "Specify related skills")
        List<@NotNull(message = "Related skills cannot be empty") Long> relatedSkillsId
) {
}