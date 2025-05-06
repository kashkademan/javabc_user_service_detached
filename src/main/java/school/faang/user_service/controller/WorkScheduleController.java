package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.service.WorkScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/work_schedule")
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;
    @PostMapping("/add_work_schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WorkScheduleDto> addWorkSchedule(long userId, WorkScheduleDto workScheduleDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(workScheduleService.addWorkSchedule(userId, workScheduleDto));
    }
}
