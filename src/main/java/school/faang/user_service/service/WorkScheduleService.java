package school.faang.user_service.service;

import school.faang.user_service.dto.WorkScheduleDto;

public interface WorkScheduleService {

    WorkScheduleDto addWorkSchedule(Long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto updateWorkSchedule(Long userId, WorkScheduleDto workScheduleDto);

    WorkScheduleDto getById(Long workScheduleId);
}
