package school.faang.user_service.controller.workschedule;

import com.amazonaws.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@Controller
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    public WorkScheduleDto addWorkSchedule(@NonNull WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);
        return workScheduleService.addWorkSchedule(userContext.getUserId(), workScheduleDto);
    }

    public WorkScheduleDto updateWorkSchedule(@NotBlank long workScheduleId, @NonNull WorkScheduleDto workScheduleDto) {
        validateWorkScheduleDto(workScheduleDto);
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, workScheduleDto);
    }

    public WorkScheduleDto getById(@NotBlank long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }

    public void validateWorkScheduleDto(@NonNull WorkScheduleDto workScheduleDto) {
        validateNullOrEmpty(workScheduleDto.id().toString(), "Id");
        validateNullOrEmpty(workScheduleDto.startTime().toString(), "StartTime");
        validateNullOrEmpty(workScheduleDto.endTime().toString(), "EndTIme");
        validateNullOrEmpty(workScheduleDto.startLunch().toString(), "StartLunch");
        validateNullOrEmpty(workScheduleDto.endLunch().toString(), "EndLunch");
        validateNullOrEmpty(workScheduleDto.timezone(), "Timezone");
    }

    private void validateNullOrEmpty(String value, String param) {
        if (StringUtils.isNullOrEmpty(value)) {
            throw new DataValidationException(param + " - пустое или равно Null.");
        }
    }
}
