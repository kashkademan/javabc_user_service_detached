package school.faang.user_service.service.workschedule;

import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;

public interface WorkScheduleService {
    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    UpdateWorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, UpdateWorkScheduleDto updateWorkScheduleDto);

    WorkScheduleDto getById(long workScheduleId);
}
