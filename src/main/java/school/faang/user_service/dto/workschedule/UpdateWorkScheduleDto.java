package school.faang.user_service.dto.workschedule;

import school.faang.user_service.service.validator.TimeRangeDto;

import java.time.LocalTime;

public record UpdateWorkScheduleDto(
        LocalTime startTime,
        LocalTime endTime,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone) {
}
