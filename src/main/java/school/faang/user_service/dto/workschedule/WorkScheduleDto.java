package school.faang.user_service.dto.workschedule;

import java.time.LocalTime;

public record WorkScheduleDto (
        Long id,
        LocalTime startTime,
        LocalTime endTIme,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone
) {
}
