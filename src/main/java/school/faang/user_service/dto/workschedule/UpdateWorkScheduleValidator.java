package school.faang.user_service.dto.workschedule;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UpdateWorkScheduleValidator {
    public void validate(UpdateWorkScheduleDto dto) {
        if (!dto.startTime().isBefore(dto.startLunch())) {
            throw new DataValidationException("Стартовое время графика должно быть раньше времени старта обеда");
        }
        if (!dto.startLunch().isBefore(dto.endLunch())) {
            throw new DataValidationException("Время старта обеда должно быть раньше времени его окончания");
        }
        if (!dto.endLunch().isBefore(dto.endTime())) {
            throw new DataValidationException("Время конца обеда должно быть раньше времени конца графика");
        }
    }
}
