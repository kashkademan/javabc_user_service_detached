package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkScheduleDto {
    private long id;
    private LocalTime startTime;
    private LocalTime endTIme;
    private LocalTime startLunch;
    private LocalTime endLunch;
    //Вот здесь вопрос, пока не понял что сделать. Надеюсь разберусь
    private String timezone; // часовой пояс в формате IANA (например, "Europe/Moscow")
    private Long userId;
}
