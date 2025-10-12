package school.faang.user_service.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.validation.ValidationConstants;
import school.faang.user_service.validation.ValidationGroups;

import java.time.LocalDateTime;

public record UpdateEventDto(
        @NotBlank(groups = ValidationGroups.OnUpdate.class)
        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE,
                groups = ValidationGroups.OnUpdate.class)
        String title,

        @NotBlank(groups = ValidationGroups.OnUpdate.class)
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE,
                groups = ValidationGroups.OnUpdate.class)
        String description,

        @NotNull(groups = ValidationGroups.OnUpdate.class)
        @Future(message = "Start date must be in future")
        LocalDateTime startDate,

        @NotNull(groups = ValidationGroups.OnUpdate.class)
        @Future(message = "End date must be in future")
        LocalDateTime endDate,

        @NotNull(groups = ValidationGroups.OnUpdate.class)
        EventType type,

        @NotNull
        EventStatus status
) {
    @AssertTrue(message = "End date must be after start date", groups = ValidationGroups.OnUpdate.class)
    public boolean isEndDateAfterStartDate() {
        return endDate == null || startDate == null || endDate.isAfter(startDate);
    }
}
