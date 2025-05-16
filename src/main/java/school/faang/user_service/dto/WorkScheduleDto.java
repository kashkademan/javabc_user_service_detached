package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkScheduleDto {
    public final Long id;
    public final LocalTime startTime;
    public final LocalTime endTime;
    public final LocalTime startLunch;
    public final LocalTime endLunch;
    public final String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
}
