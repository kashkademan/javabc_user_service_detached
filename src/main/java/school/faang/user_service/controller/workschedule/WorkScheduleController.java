package school.faang.user_service.controller.workschedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.facade.workschedule.WorkScheduleFacade;
import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;

@Tag(name = "Work Schedule Controller", description = "APIs for managing work schedules")
@RequiredArgsConstructor
@RequestMapping("/work-schedules")
@RestController
public class WorkScheduleController {
    private final WorkScheduleFacade workScheduleFacade;

    @Operation(summary = "Add work schedule")
    @PostMapping
    public WorkScheduleDto addWorkSchedule(@Valid @RequestBody WorkScheduleCreateDto workScheduleCreateDto) {
        return workScheduleFacade.addWorkSchedule(workScheduleCreateDto);
    }

    @Operation(summary = "Update work schedule")
    @PatchMapping("/{id}")
    public WorkScheduleDto updateWorkSchedule(@PathVariable("id") long workScheduleId,
                                              @RequestBody WorkScheduleUpdateDto workScheduleUpdateDto) {
        return workScheduleFacade.updateWorkSchedule(workScheduleId, workScheduleUpdateDto);
    }

    @Operation(summary = "Get work schedule by ID")
    @GetMapping("/{id}")
    public WorkScheduleDto getById(@PathVariable("id") long workScheduleId) {
        return workScheduleFacade.getById(workScheduleId);
    }

    @Operation(summary = "Delete work schedule")
    @DeleteMapping("/{id}")
    public void deleteWorkSchedule(@PathVariable("id") long workScheduleId) {
        workScheduleFacade.deleteWorkSchedule(workScheduleId);
    }
}