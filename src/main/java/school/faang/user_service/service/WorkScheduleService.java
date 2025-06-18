package school.faang.user_service.service;

import school.faang.user_service.entity.WorkSchedule;

public interface WorkScheduleService {
    WorkSchedule addWorkSchedule(WorkSchedule workSchedule);
    WorkSchedule updateWorkScheduleDto(WorkSchedule workSchedule);
    WorkSchedule getById(long workScheduleId);
}
