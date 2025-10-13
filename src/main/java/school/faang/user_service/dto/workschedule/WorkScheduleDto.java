package school.faang.user_service.dto.workschedule;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record WorkScheduleDto(
        long id,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone) {
}
