package school.faang.user_service.util;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class WorkScheduleDtoValidator {

    public void validateDto(WorkScheduleDto workScheduleDto) {
        checkValidTimeLine(workScheduleDto);
    }

    private void checkValidTimeLine(WorkScheduleDto workScheduleDto) {
        if (workScheduleDto.getStartTime().isBefore(workScheduleDto.getStartLunch())
            && workScheduleDto.getStartLunch().isBefore(workScheduleDto.getEndLunch())
            && workScheduleDto.getEndLunch().isBefore(workScheduleDto.getEndTime())) {
            return;
        }
        throw new DataValidationException("startTime should be before startLunch. " +
                                          "both of them should be before endLunch. And all of them should be before endTime");
    }
}

