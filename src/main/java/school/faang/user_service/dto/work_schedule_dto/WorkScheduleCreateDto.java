package school.faang.user_service.dto.work_schedule_dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@RequiredArgsConstructor
public class WorkScheduleCreateDto{
    private long id;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startLunch;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endLunch;
    private String timezone;
}
