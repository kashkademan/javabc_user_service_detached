package school.faang.user_service.service;

import school.faang.user_service.dto.work_schedule_dto.WorkScheduleCreateDto;
import school.faang.user_service.entity.WorkSchedule;

public interface WorkScheduleService {
    WorkSchedule addWorkSchedule(WorkSchedule workSchedule);
    WorkSchedule updateWorkScheduleDto(WorkSchedule workSchedule);
    WorkSchedule getById(long workScheduleId);
}
