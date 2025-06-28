package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Setter
@Builder
public class WorkScheduleDto {
    public Long id;
    @NotNull(message = "All fields should be filled")
    public LocalTime startTime;
    @NotNull(message = "All fields should be filled")
    public LocalTime endTime;
    @NotNull(message = "All fields should be filled")
    public LocalTime startLunch;
    @NotNull(message = "All fields should be filled")
    public LocalTime endLunch;
    @NotNull(message = "All fields should be filled")
    public String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
}
