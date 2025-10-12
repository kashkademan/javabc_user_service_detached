package school.faang.user_service.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.ValidationConstants;
import school.faang.user_service.validation.ValidationGroups;

import java.time.LocalDateTime;

public record CreateEventDto(
        @NotBlank(message = "Title must not be blank", groups = ValidationGroups.OnCreate.class)
        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE,
                groups = ValidationGroups.OnCreate.class)
        String title,

        @NotBlank(message = "Description must not be blank", groups = ValidationGroups.OnCreate.class)
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE,
                groups = ValidationGroups.OnCreate.class)
        String description,

        @NotNull(message = "Start date must not be null", groups = ValidationGroups.OnCreate.class)
        @Future(message = "Start date must be in future", groups = ValidationGroups.OnCreate.class)
        LocalDateTime startDate,

        @NotNull(message = "End date must not be null", groups = ValidationGroups.OnCreate.class)
        @Future(message = "End date must be in future", groups = ValidationGroups.OnCreate.class)
        LocalDateTime endDate,

        @NotNull(message = "Event type must not be null", groups = ValidationGroups.OnCreate.class)
        EventType type
) {
    @AssertTrue(message = "End date must be after start date", groups = ValidationGroups.OnCreate.class)
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
