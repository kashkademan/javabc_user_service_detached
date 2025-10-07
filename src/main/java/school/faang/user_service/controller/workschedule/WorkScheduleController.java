package school.faang.user_service.controller.workschedule;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.service.workschedule.WorkScheduleService;

@RequestMapping("/work-schedules")
@RestController
@RequiredArgsConstructor
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final UserContext userContext;

    @PostMapping
    public WorkScheduleDto addWorkSchedule(@RequestBody WorkScheduleDto workScheduleDto) {
        return workScheduleService.addWorkSchedule(userContext.getUserId(), workScheduleDto);
    }

    @PutMapping("/{id}")
    public WorkScheduleDto updateWorkSchedule(@PathVariable("id") long workScheduleId, @RequestBody UpdateWorkScheduleDto updateWorkScheduleDto) {
        return workScheduleService.updateWorkSchedule(userContext.getUserId(), workScheduleId, updateWorkScheduleDto);
    }

    @GetMapping("/{id}")
    public WorkScheduleDto getById(@PathVariable("id") long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }
}