package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;


public record WorkScheduleDto(
        @NotNull
        long id,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTIme,
        @NotNull
        LocalTime startLunch,
        @NotNull
        LocalTime endLunch,
        @NotNull
        String timezone
) {

}