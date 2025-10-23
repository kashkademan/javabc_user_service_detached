package school.faang.user_service.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.ValidationConstants;

import java.time.LocalDateTime;

public record UpdateEventDto(
        @NotBlank
        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE)
        String title,

        @NotBlank
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        @NotNull
        @Future(message = "Start date must be in future")
        LocalDateTime startDate,

        @NotNull
        @Future(message = "End date must be in future")
        LocalDateTime endDate,

        @NotNull
        EventType type,

        @NotNull
        EventStatus status
) {
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        return endDate == null || startDate == null || endDate.isAfter(startDate);
    }
}
