package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Setter
@Builder
public class WorkScheduleDto {
    public Long id;
    public LocalTime startTime;
    public LocalTime endTime;
    public LocalTime startLunch;
    public LocalTime endLunch;
    public String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
}
