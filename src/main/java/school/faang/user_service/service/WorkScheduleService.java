package school.faang.user_service.service;

import school.faang.user_service.dto.WorkScheduleCreateDto;

public interface WorkScheduleService {
    WorkScheduleCreateDto addWorkSchedule(WorkScheduleCreateDto workScheduleCreateDto);
    WorkScheduleCreateDto updateWorkScheduleDto(WorkScheduleCreateDto workScheduleCreateDto);
    WorkScheduleCreateDto getById(long workScheduleId);
}
