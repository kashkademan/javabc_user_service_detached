package school.faang.user_service.service.validator;

import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

public class UpdateWorkScheduleValidator {
    public static void validate(UpdateWorkScheduleDto dto) {
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
