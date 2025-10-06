package school.faang.user_service.controller.workschedule;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.workschedule.WorkScheduleService;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    public WorkScheduleDto addWorkSchedule(@Valid WorkScheduleDto workScheduleDto) {
        validateUserAccess(userContext.getUserId(), workScheduleDto);
        return workScheduleService.addWorkSchedule(workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(long workScheduleId, @Valid WorkScheduleDto workScheduleDto) {
        validateUserAccess(userContext.getUserId(), workScheduleDto);
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, workScheduleDto);
    }

    public WorkScheduleDto getById(long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }


    private void validateUserAccess(long userId, @Valid WorkScheduleDto workScheduleDto) {
        if (!Objects.equals(workScheduleDto.id(), userId)) {
            log.warn("Access denied: user {} tried to access schedule {}", userId, workScheduleDto.id()); // на cross-review сказали, что не стоит логгировать ожидаемое поведение системы. Оставить или убрать?
            throw new ForbiddenException(
                    String.format("User %d does not have access to work schedule %d", userId, workScheduleDto.id())
            );
        }
    }

}
