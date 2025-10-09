package school.faang.user_service.service.validator;

import java.time.LocalTime;

public interface TimeRangeDto {
    LocalTime startTime();

    LocalTime endTime();

    LocalTime endLunch();

    LocalTime startLunch();

}
