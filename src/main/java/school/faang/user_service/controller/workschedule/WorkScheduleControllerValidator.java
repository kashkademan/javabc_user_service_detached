package school.faang.user_service.controller.workschedule;

import school.faang.user_service.dto.workschedule.WorkScheduleDto;

public interface WorkScheduleControllerValidator {

    void validateUserAccess(long userId, WorkScheduleDto workScheduleDto);
}
