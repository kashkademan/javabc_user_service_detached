package school.faang.user_service.dto.workschedule;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateWorkScheduleDto(
        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @Nullable
        LocalTime startLunch,

        @Nullable
        LocalTime endLunch,

        @Nullable
        String timezone) {
}
