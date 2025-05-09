package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkScheduleCreateDto {
    private long id;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startLunch;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endLunch;
    private String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
}
