package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateWorkScheduleDto(@NotNull(message = "Start time is required") LocalTime startTime,

                                    @NotNull(message = "End time is required") LocalTime endTime,

                                    @NotNull(message = "Lunch start time is required") LocalTime startLunch,

                                    @NotNull(message = "Lunch end time is required") LocalTime endLunch,

                                    @NotNull(message = "Timezone is required") String timezone) {
}
