package school.faang.user_service.service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class WorkScheduleValidator {
    public void validate(WorkScheduleDto dto) {
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
