package school.faang.user_service.controller.workschedule;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;


@Slf4j
@RequiredArgsConstructor
@Controller
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    public WorkScheduleDto addWorkSchedule(@Valid WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userContext.getUserId(), workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, @Valid WorkScheduleDto workScheduleDto) {
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, workScheduleDto);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}
