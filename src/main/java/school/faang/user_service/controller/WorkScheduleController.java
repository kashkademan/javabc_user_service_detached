package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleCreateDto;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleDto;
import school.faang.user_service.dto.work_schedule_dto.WorkScheduleUpdateDto;
import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.mapper.WorkScheduleMapper;
import school.faang.user_service.service.WorkScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/work-schedule")
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleMapper workScheduleMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WorkScheduleDto> addWorkSchedule(
            @RequestBody WorkScheduleCreateDto workScheduleCreateDto) {
        WorkSchedule workSchedule = workScheduleMapper.toWorkSchedule(workScheduleCreateDto);
        WorkSchedule newWorkSchedule = workScheduleService.addWorkSchedule(workSchedule);
        WorkScheduleDto resultWorkScheduleDto = workScheduleMapper.toWorkScheduleDto(newWorkSchedule);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(resultWorkScheduleDto);
    }

    @GetMapping("/{workScheduleId}")
    public WorkScheduleDto getById(@PathVariable("workScheduleId") long workScheduleId) {
        return workScheduleMapper.toWorkScheduleDto(workScheduleService
                .getById(workScheduleId));
    }

    @PatchMapping
    public WorkScheduleUpdateDto updateWorkSchedule(@RequestBody WorkScheduleUpdateDto workScheduleUpdateDto) {
        WorkSchedule workSchedule = workScheduleMapper.toWorkScheduleUpdate(workScheduleUpdateDto);
        WorkSchedule updateWorkSchedule = workScheduleService.updateWorkScheduleDto(workSchedule);
        return workScheduleMapper.toWorkScheduleUpdateDto(updateWorkSchedule);
    }
}
