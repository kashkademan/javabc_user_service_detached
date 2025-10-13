package school.faang.user_service.dto.workschedule;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record WorkScheduleCreateDto(
        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @NotNull
        LocalTime startLunch,

        @NotNull
        LocalTime endLunch,

        @Nullable
        String timezone) {
    public WorkScheduleCreateDto {
        timezone = timezone != null ? timezone : "UTC";
    }
}
