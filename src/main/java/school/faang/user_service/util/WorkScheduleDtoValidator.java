package school.faang.user_service.util;

import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

public class WorkScheduleDtoValidator {

    public static void validateDto(WorkScheduleDto workScheduleDto) {
        checkValidTimeLine(workScheduleDto);
        checkValidFields(workScheduleDto);
    }

    private static void checkValidTimeLine(WorkScheduleDto workScheduleDto) {
        if (workScheduleDto.getStartTime().isBefore(workScheduleDto.getStartLunch())
                && workScheduleDto.getStartLunch().isBefore(workScheduleDto.getEndLunch())
                && workScheduleDto.getEndLunch().isBefore(workScheduleDto.getEndTime())) {
            return;
        }
        throw new DataValidationException("startTime should be before startLunch. " +
                "both of them should be before endLunch. And all of them should be before endTime");
    }

    private static void checkValidFields(WorkScheduleDto workScheduleDto) {
        if (workScheduleDto.getId() == 0
                || workScheduleDto.getTimezone() == null
                || workScheduleDto.getStartTime() == null
                || workScheduleDto.getStartLunch() == null
                || workScheduleDto.getEndLunch() == null
                || workScheduleDto.getEndTime() == null) {
            throw new DataValidationException("All fields should be filled");
        }
    }
}

