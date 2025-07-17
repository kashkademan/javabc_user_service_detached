package school.faang.user_service.controller.workschedule;

import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.workschedule.WorkScheduleService;

public class WorkScheduleController {
    private WorkScheduleService workScheduleService;
    private UserContext userContext;

    public WorkScheduleDto addWorkSchedule(WorkScheduleDto dto) {
        if (!(dto.startTime().isBefore(dto.startLunch()) && dto.endLunch().isBefore(dto.endTime()))) {
            throw new DataValidationException("Не коректные данные!");
        }

        return workScheduleService.addWorkSchedule(userContext.getUserId(), dto);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, WorkScheduleDto dto) {
        if (!(dto.startTime().isBefore(dto.startLunch())
                && dto.startLunch().isBefore(dto.endLunch())
                && dto.endLunch().isBefore(dto.endTime()))) {
            //startTime < (меньше/раньше чем) startLunch < endLunch < endTime
            throw new DataValidationException("Не коректные данные!");
        }
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, dto);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}
