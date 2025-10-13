package school.faang.user_service.dto.workschedule;

import jakarta.annotation.Nullable;

import java.time.LocalTime;

public record WorkScheduleUpdateDto(
        @Nullable
        LocalTime startTime,

        @Nullable
        LocalTime endTime,

        @Nullable
        LocalTime startLunch,

        @Nullable
        LocalTime endLunch,

        @Nullable
        String timezone) {
}
