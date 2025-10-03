package school.faang.user_service.controller.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    public WorkScheduleDto addWorkSchedule(WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userContext.getUserId(), workScheduleDto);
    }

    public UpdateWorkScheduleDto updateWorkSchedule(long workScheduleId, UpdateWorkScheduleDto updateWorkScheduleDto) {
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, updateWorkScheduleDto);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}