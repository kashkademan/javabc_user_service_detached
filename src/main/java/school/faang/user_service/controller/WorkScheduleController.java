package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.WorkScheduleCreateDto;
import school.faang.user_service.service.WorkScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/work_schedule")
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WorkScheduleCreateDto> addWorkSchedule(@RequestBody WorkScheduleCreateDto workScheduleCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(workScheduleService.addWorkSchedule(workScheduleCreateDto));
    }

    @GetMapping("/{workScheduleId}")
    public WorkScheduleCreateDto getById(@PathVariable("workScheduleId") long workScheduleId) {
        return workScheduleService.getById(workScheduleId);
    }

    @PutMapping("/update")
    public WorkScheduleCreateDto updateWorkSchedule(@RequestBody WorkScheduleCreateDto workScheduleCreateDto) {
        return workScheduleService.updateWorkScheduleDto(workScheduleCreateDto);
    }
}
