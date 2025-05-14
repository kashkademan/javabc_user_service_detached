package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.util.WorkScheduleDtoValidator;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleDto addWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        WorkScheduleDtoValidator.validateDto(workScheduleDto);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(Long userId, WorkScheduleDto workScheduleDto) {
        WorkScheduleDtoValidator.validateDto(workScheduleDto);
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto getById(Long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }

}
