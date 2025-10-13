package school.faang.user_service.dto.workschedule;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.time.LocalTime;

@Builder
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
