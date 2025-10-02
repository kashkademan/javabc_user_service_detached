package school.faang.user_service.service.workschedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.Objects;

@Slf4j
@Component
public class WorkScheduleServiceValidationImpl implements WorkScheduleServiceValidation {

    @Override
    public void validateWorkScheduleDto(WorkScheduleDto workScheduleDto) {
        if (!(workScheduleDto.startTime().isBefore(workScheduleDto.startLunch())
                && workScheduleDto.startLunch().isBefore(workScheduleDto.endLunch())
                && workScheduleDto.endLunch().isBefore(workScheduleDto.endTime()))) {
            log.error("Illegal schedule or lunch time. startTime: {}; endTime: {}; startLunch: {}; endLunch: {}",
                    workScheduleDto.startTime(), workScheduleDto.endTime(),
                    workScheduleDto.startLunch(), workScheduleDto.endLunch());
            throw new DataValidationException("Illegal schedule or lunch time.");
        }
    }

    @Override
    public void validateUserAccess(long userId, WorkSchedule workSchedule) {
        if (!Objects.equals(workSchedule.getId(), userId)) {
            log.error("user_id ({}) doesn't match workSchedule user_id ({})", workSchedule.getId(), userId);
            throw new ForbiddenException("User does not belong to this work schedule.");
        }
    }
}
