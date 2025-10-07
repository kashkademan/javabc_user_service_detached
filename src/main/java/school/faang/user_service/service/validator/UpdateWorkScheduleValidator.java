package school.faang.user_service.service.validator;

import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

public class UpdateWorkScheduleValidator {
    public static void validate(UpdateWorkScheduleDto dto) {
        if (!dto.startTime().isBefore(dto.startLunch())) {
            throw new DataValidationException("Start time must be before lunch start time");
        }
        if (!dto.startLunch().isBefore(dto.endLunch())) {
            throw new DataValidationException("Lunch start time must be before lunch end time");
        }
        if (!dto.endLunch().isBefore(dto.endTime())) {
            throw new DataValidationException("Lunch end time must be before work end time");
        }
    }
}
