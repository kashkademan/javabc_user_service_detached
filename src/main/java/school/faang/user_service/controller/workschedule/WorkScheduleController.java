package school.faang.user_service.controller.workschedule;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Slf4j
@Controller
public class WorkScheduleController {
    WorkScheduleService workScheduleService;
    UserContext userContext;
    WorkScheduleControllerValidator validator;

    public WorkScheduleDto addWorkSchedule(WorkScheduleDto workScheduleDto) {
        validator.validateUserAccess(userContext.getUserId(), workScheduleDto);
        return workScheduleService.addWorkSchedule(workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, WorkScheduleDto workScheduleDto) {
        validator.validateUserAccess(userContext.getUserId(), workScheduleDto);
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, workScheduleDto);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}
