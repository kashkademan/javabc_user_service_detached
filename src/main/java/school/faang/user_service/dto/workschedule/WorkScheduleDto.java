package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.service.validator.TimeRangeDto;

import java.time.LocalTime;

public record WorkScheduleDto(
        long id,

        @NotNull(message = "Start time cannot be null")
        LocalTime startTime,

        @NotNull(message = "End time cannot be null")
        LocalTime endTime,

        LocalTime startLunch,

        LocalTime endLunch,

        String timezone) implements TimeRangeDto {
}
