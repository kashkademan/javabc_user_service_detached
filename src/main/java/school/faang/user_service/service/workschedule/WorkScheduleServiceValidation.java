package school.faang.user_service.service.workschedule;

import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.entity.user.WorkSchedule;

public interface WorkScheduleServiceValidation {
    void validateWorkScheduleDto(WorkScheduleDto workScheduleDto);

    void validateUserAccess(long userId, WorkSchedule workSchedule);
}
