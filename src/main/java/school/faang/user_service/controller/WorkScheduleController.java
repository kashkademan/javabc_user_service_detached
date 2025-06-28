package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;
import school.faang.user_service.util.WorkScheduleDtoValidator;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;
    private final WorkScheduleDtoValidator validator;

    public WorkScheduleDto addWorkSchedule(Long userId, @Valid WorkScheduleDto workScheduleDto) {
        validator.validateDto(workScheduleDto);
        return workScheduleService.addWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(Long userId, @Valid WorkScheduleDto workScheduleDto) {
        validator.validateDto(workScheduleDto);
        return workScheduleService.updateWorkSchedule(userId, workScheduleDto);
    }

    public WorkScheduleDto getById(Long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}
